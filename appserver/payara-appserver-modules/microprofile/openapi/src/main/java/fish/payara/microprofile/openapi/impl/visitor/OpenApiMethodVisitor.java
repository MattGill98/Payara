package fish.payara.microprofile.openapi.impl.visitor;

import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.MethodVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.Opcodes;

public final class OpenApiMethodVisitor extends MethodVisitor {

    public OpenApiMethodVisitor() {
        super(Opcodes.ASM5);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return super.visitAnnotation(desc, visible);
    }
}