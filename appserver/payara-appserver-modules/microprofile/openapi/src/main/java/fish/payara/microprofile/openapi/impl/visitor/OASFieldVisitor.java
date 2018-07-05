package fish.payara.microprofile.openapi.impl.visitor;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

public final class OASFieldVisitor extends FieldVisitor {

    public OASFieldVisitor() {
        super(Opcodes.ASM5);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return super.visitAnnotation(desc, visible);
    }
}