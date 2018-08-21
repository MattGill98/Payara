package fish.payara.microprofile.openapi.impl.visitor;

import static fish.payara.microprofile.openapi.impl.visitor.OASContext.getSimpleName;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.parameters.RequestBody;
import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.Label;
import org.glassfish.hk2.external.org.objectweb.asm.MethodVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.Type;

import fish.payara.microprofile.openapi.impl.model.PathItemImpl;
import fish.payara.microprofile.openapi.impl.model.media.ContentImpl;
import fish.payara.microprofile.openapi.impl.model.media.MediaTypeImpl;
import fish.payara.microprofile.openapi.impl.model.media.SchemaImpl;
import fish.payara.microprofile.openapi.impl.model.parameters.RequestBodyImpl;
import fish.payara.microprofile.openapi.impl.processor.ASMProcessor;

public final class OASMethodVisitor extends MethodVisitor {

    private static final Logger LOGGER = Logger.getLogger(OASMethodVisitor.class.getName());

    private final OASContext context;

    private String httpMethodName;
    private Operation currentOperation;

    private int parameterCount;

    private String parameterAnnotation;

    public OASMethodVisitor(OASContext context, Operation currentOperation) {
        this(context, currentOperation, 0);
    }

    public OASMethodVisitor(OASContext context, Operation currentOperation, int parameterCount) {
        super(ASMProcessor.ASM_VERSION);
        this.context = context;
        this.currentOperation = currentOperation;
        this.parameterCount = parameterCount;
    }

	@Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        if (!"this".equals(name) && parameterCount > 0) {
            parameterCount --;
            visitParameter(name, Type.getType(desc).getClassName());
            parameterAnnotation = null;
        }
        super.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        parameterAnnotation = Type.getType(desc).getClassName();
        return super.visitParameterAnnotation(parameter, desc, visible);
    }

    public void visitParameter(String name, String type) {
        System.out.println("Visited endpoint parameter " + name + " of type: " + type + " with annotation: " + parameterAnnotation);

        // If the variable type is a schema, add it to the document
        if (context.containsSchema(type)) {
            SchemaImpl classSchema = context.getSchema(type);
            classSchema.setSchemaEnabled(true);
            context.getApi().getComponents().addSchema(classSchema.getSchemaName(), classSchema);
        } else {
            context.addSchema(type, new SchemaImpl().schemaName(getSimpleName(type)));
        }

        if (parameterAnnotation == null || "org.eclipse.microprofile.openapi.annotations.media.Schema".equals(parameterAnnotation)) {
            currentOperation.setRequestBody(createRequestBody(type));
        }
    }

    private RequestBody createRequestBody(String type) {
		return new RequestBodyImpl().content(new ContentImpl().addMediaType(MediaType.WILDCARD, new MediaTypeImpl().schema(new SchemaImpl().ref(getSimpleName(type)))));
	}

	@Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (desc != null) {
            String className = Type.getType(desc).getClassName();
            switch (className) {
                case "javax.ws.rs.Path":
                    return new PathOASAnnotationVisitor(context, true);
                case "javax.ws.rs.GET":
                case "javax.ws.rs.POST":
                case "javax.ws.rs.PUT":
                case "javax.ws.rs.DELETE":
                case "javax.ws.rs.PATCH":
                case "javax.ws.rs.OPTIONS":
                case "javax.ws.rs.HEAD":
                case "javax.ws.rs.TRACE":
                    httpMethodName = className;
                    return new HttpMethodOASAnnotationVisitor(context, className);
                case "javax.ws.rs.Produces":
                    return new ProducesOASAnnotationVisitor(context, currentOperation.getResponses());
                case "org.eclipse.microprofile.openapi.annotations.responses.APIResponse":
                case "org.eclipse.microprofile.openapi.annotations.responses.APIResponses":
                    return new APIResponseOASAnnotationVisitor(context, currentOperation.getResponses());
            }
        }

        return super.visitAnnotation(desc, visible);
    }

    @Override
    public void visitEnd() {
        if (context.isOperationValid(httpMethodName != null)) {
            LOGGER.log(Level.INFO, "Operation found at path: " + context.getPath());
            PathItem pathItem = context.getApi().getPaths().get(context.getPath());
            if (pathItem == null) {
                pathItem = new PathItemImpl();
                context.getApi().getPaths().addPathItem(context.getPath(), pathItem);
            }
            switch (httpMethodName) {
                case "javax.ws.rs.GET":
                    pathItem.GET(currentOperation); break;
                case "javax.ws.rs.POST":
                    pathItem.POST(currentOperation); break;
                case "javax.ws.rs.PUT":
                    pathItem.PUT(currentOperation); break;
                case "javax.ws.rs.DELETE":
                    pathItem.DELETE(currentOperation); break;
                case "javax.ws.rs.PATCH":
                    pathItem.PATCH(currentOperation); break;
                case "javax.ws.rs.OPTIONS":
                    pathItem.OPTIONS(currentOperation); break;
                case "javax.ws.rs.HEAD":
                    pathItem.HEAD(currentOperation); break;
                case "javax.ws.rs.TRACE":
                    pathItem.TRACE(currentOperation); break;
            }
        }
        super.visitEnd();
    }
}