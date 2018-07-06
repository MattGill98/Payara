package fish.payara.microprofile.openapi.impl.visitor;

import static fish.payara.microprofile.openapi.impl.visitor.OASContext.getClassName;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;
import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.ClassVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.FieldVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.MethodVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.Opcodes;

import fish.payara.microprofile.openapi.impl.model.OperationImpl;
import fish.payara.microprofile.openapi.impl.model.media.ContentImpl;
import fish.payara.microprofile.openapi.impl.model.media.MediaTypeImpl;
import fish.payara.microprofile.openapi.impl.model.media.SchemaImpl;
import fish.payara.microprofile.openapi.impl.model.responses.APIResponseImpl;

public final class OASClassVisitor extends ClassVisitor {

    private static final Logger LOGGER = Logger.getLogger(OASClassVisitor.class.getName());

    private final OASContext context;

    private String className;

    public OASClassVisitor(OASContext context) {
        super(Opcodes.ASM5);
        this.context = context;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name;
        LOGGER.log(Level.INFO, "Entering class: " + name);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        context.setAnnotationName(getClassName(desc));

        if (ApplicationPath.class.getName().equals(getClassName(desc))) {
            return new ApplicationPathOASAnnotationVisitor(context);
        } else if (Path.class.getName().equals(getClassName(desc))) {
            return new PathOASAnnotationVisitor(context, false);
        }

        return super.visitAnnotation(desc, visible);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return new OASFieldVisitor();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        context.setCurrentOperation(getDefaultOperation(name, getClassName(desc)));
        return new OASMethodVisitor(context);
    }

    @Override
    public void visitEnd() {
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