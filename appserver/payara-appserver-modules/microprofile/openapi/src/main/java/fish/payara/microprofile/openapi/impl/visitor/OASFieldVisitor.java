package fish.payara.microprofile.openapi.impl.visitor;

import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.FieldVisitor;

import fish.payara.microprofile.openapi.impl.processor.ASMProcessor;

public final class OASFieldVisitor extends FieldVisitor {

    public OASFieldVisitor() {
        super(ASMProcessor.ASM_VERSION);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return super.visitAnnotation(desc, visible);
    }
}