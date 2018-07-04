package fish.payara.microprofile.openapi.impl.visitor;

import static fish.payara.microprofile.openapi.impl.visitor.VisitorContext.getClassName;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.MethodVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.Opcodes;

public final class OpenApiMethodVisitor extends MethodVisitor {

    private final VisitorContext context;

    public OpenApiMethodVisitor(VisitorContext context) {
        super(Opcodes.ASM5);
        this.context = context;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {

        if (Path.class.getName().equals(getClassName(desc))) {
            return new PathAnnotationVisitor(context, false);
        }
        if (GET.class.getName().equals(getClassName(desc))) {
            return new GETAnnotationVisitor(context);
        }

        return super.visitAnnotation(desc, visible);
    }
}