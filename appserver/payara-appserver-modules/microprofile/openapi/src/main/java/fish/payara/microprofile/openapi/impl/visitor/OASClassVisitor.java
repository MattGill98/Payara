package fish.payara.microprofile.openapi.impl.visitor;

import static fish.payara.microprofile.openapi.impl.visitor.OASContext.getSimpleName;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;
import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.ClassVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.FieldVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.MethodVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.Type;

import fish.payara.microprofile.openapi.impl.model.OperationImpl;
import fish.payara.microprofile.openapi.impl.model.media.ContentImpl;
import fish.payara.microprofile.openapi.impl.model.media.MediaTypeImpl;
import fish.payara.microprofile.openapi.impl.model.media.SchemaImpl;
import fish.payara.microprofile.openapi.impl.model.responses.APIResponseImpl;
import fish.payara.microprofile.openapi.impl.processor.ASMProcessor;

public final class OASClassVisitor extends ClassVisitor {

    private static final Logger LOGGER = Logger.getLogger(OASClassVisitor.class.getName());

    private final OASContext context;

    private String className;

    private SchemaImpl classSchema;

    public OASClassVisitor(OASContext context) {
        super(ASMProcessor.ASM_VERSION);
        this.context = context;
        this.classSchema = new SchemaImpl();
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = Type.getObjectType(name).getClassName();
        LOGGER.log(Level.INFO, "Entering class: " + className);

        // If the schema should exist, find it
        if (context.containsSchema(className)) {
            classSchema = context.getSchema(className);
            classSchema.setSchemaEnabled(true);
        } else {
            // Set the schema name
            String simpleClassname = getSimpleName(className);
            classSchema.setSchemaName(simpleClassname);
        }
        context.addSchema(className, classSchema);

        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (desc != null) {
            String className = Type.getType(desc).getClassName();
            switch (className) {
                case "javax.ws.rs.ApplicationPath":
                    return new ApplicationPathOASAnnotationVisitor(context);
                case "javax.ws.rs.Path":
                    return new PathOASAnnotationVisitor(context, false);
                case "org.eclipse.microprofile.openapi.annotations.media.Schema":
                    classSchema.setSchemaEnabled(true);
                    return new SchemaOASAnnotationVisitor(context, classSchema);
            }
        }
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (!"this$0".equals(name)) {
            classSchema.addProperty(name, new SchemaImpl().type(getSchemaType(Type.getType(desc).getClassName())));
        }
        return new OASFieldVisitor();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new OASMethodVisitor(context, getDefaultOperation(name, Type.getReturnType(desc).getClassName()), "<init>".equals(name)? new Type[0]: Type.getArgumentTypes(desc));
    }

    @Override
    public void visitEnd() {
        if (classSchema.isSchemaEnabled()) {
            context.getApi().getComponents().addSchema(classSchema.getSchemaName(), classSchema);
        }
        LOGGER.log(Level.INFO, "Leaving class: " + className);
        super.visitEnd();
    }

    private Operation getDefaultOperation(String operationId, String returnType) {
        // Create a new operation with the given name
        Operation operation = new OperationImpl().operationId(operationId);

        // Add a default response
        APIResponse defaultResponse = new APIResponseImpl()
                .description("Default Response.")
                .content(new ContentImpl()
                        .addMediaType(MediaType.WILDCARD, new MediaTypeImpl()
                                .schema(new SchemaImpl()
                                        .type(getSchemaType(returnType)))));
        operation.getResponses().addApiResponse(APIResponses.DEFAULT, defaultResponse);

        return operation;
    }

    /**
     * Finds the {@link SchemaType} that corresponds to a given class.
     * 
     * @param type the class to map.
     * @return the schema type the class corresponds to.
     */
    private static SchemaType getSchemaType(String type) {
        if (type == null) {
            return SchemaType.OBJECT;
        }
        if (type.equals("java.lang.String")) {
            return SchemaType.STRING;
        }
        if (type.equals("java.lang.Boolean") || type.equals("boolean")) {
            return SchemaType.BOOLEAN;
        }
        if (type.equals("java.lang.Integer") || type.equals("int")) {
            return SchemaType.INTEGER;
        }
        if (type.equals("java.lang.Short") || type.equals("short")
        || type.equals("java.lang.Long") || type.equals("long")
        || type.equals("java.lang.Float") || type.equals("float")
        || type.equals("java.lang.Double") || type.equals("double")) {
            return SchemaType.NUMBER;
        }
        if (type.contains("[]")) {
            return SchemaType.ARRAY;
        }
        // Check for array
        return SchemaType.OBJECT;
    }

}