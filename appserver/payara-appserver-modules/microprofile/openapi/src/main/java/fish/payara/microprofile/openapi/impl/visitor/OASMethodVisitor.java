package fish.payara.microprofile.openapi.impl.visitor;

import static fish.payara.microprofile.openapi.impl.visitor.OASContext.getClassName;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.MethodVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.Opcodes;

import fish.payara.microprofile.openapi.impl.model.PathItemImpl;

public final class OASMethodVisitor extends MethodVisitor {

    private static final Logger LOGGER = Logger.getLogger(OASMethodVisitor.class.getName());

    private final OASContext context;

    private String httpMethodName;
    private Operation currentOperation;

    public OASMethodVisitor(OASContext context, Operation currentOperation) {
        super(Opcodes.ASM5);
        this.context = context;
        this.currentOperation = currentOperation;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (desc != null) {
            String className = getClassName(desc);
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