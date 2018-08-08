package fish.payara.microprofile.openapi.impl.visitor;

import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;

import fish.payara.microprofile.openapi.impl.processor.ASMProcessor;

public abstract class OASAnnotationVisitor extends AnnotationVisitor {

    protected final OASContext context;

    public OASAnnotationVisitor(OASContext context) {
        super(ASMProcessor.ASM_VERSION);
        this.context = context;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}