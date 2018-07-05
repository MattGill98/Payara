package fish.payara.microprofile.openapi.impl.visitor;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

public abstract class OASAnnotationVisitor extends AnnotationVisitor {

    protected final OASContext context;

    public OASAnnotationVisitor(OASContext context) {
        super(Opcodes.ASM5);
        this.context = context;
    }

    @Override
    public void visitEnd() {
        context.clearAnnotationName();
        super.visitEnd();
    }
}