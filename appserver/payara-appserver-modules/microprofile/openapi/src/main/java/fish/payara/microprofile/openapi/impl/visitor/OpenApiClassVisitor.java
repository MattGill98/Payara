package fish.payara.microprofile.openapi.impl.visitor;

import static fish.payara.microprofile.openapi.impl.visitor.VisitorContext.getClassName;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;

import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.ClassVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.FieldVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.MethodVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.Opcodes;

public final class OpenApiClassVisitor extends ClassVisitor {

    private static final Logger LOGGER = Logger.getLogger(OpenApiClassVisitor.class.getName());

    private final VisitorContext context;

    public OpenApiClassVisitor(VisitorContext context) {
        super(Opcodes.ASM5);
        this.context = context;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        context.setClassName(getClassName(name));
        LOGGER.log(Level.INFO, "Entering class: " + context.getClassName());
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {

        if (ApplicationPath.class.getName().equals(getClassName(desc))) {
            return new ApplicationPathAnnotationVisitor(context);
        } else if (Path.class.getName().equals(getClassName(desc))) {
            return new PathAnnotationVisitor(context, false);
        }

        return super.visitAnnotation(desc, visible);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return new OpenApiFieldVisitor();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        context.setMethodName(name);
        return new OpenApiMethodVisitor(context);
    }

    @Override
    public void visitEnd() {
        LOGGER.log(Level.INFO, "Leaving class: " + context.getClassName());
        context.setClassName(null);
        super.visitEnd();
    }

}