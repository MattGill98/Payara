package fish.payara.microprofile.openapi.impl.visitor;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.ClassVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.FieldVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.MethodVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.Opcodes;

public final class OpenApiClassVisitor extends ClassVisitor {

    private static final Logger LOGGER = Logger.getLogger(OpenApiClassVisitor.class.getName());

    private String className;

    public OpenApiClassVisitor() {
        super(Opcodes.ASM5);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = getClassName(name);
        LOGGER.log(Level.INFO, "Entering class: " + className);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return new OpenApiFieldVisitor();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new OpenApiMethodVisitor();
    }

    @Override
    public void visitEnd() {
        LOGGER.log(Level.INFO, "Leaving class: " + className);
        super.visitEnd();
    }

    // PRIVATE METHODS

    private String getClassName(String name) {
        return name.replace("/", ".");
    }
}