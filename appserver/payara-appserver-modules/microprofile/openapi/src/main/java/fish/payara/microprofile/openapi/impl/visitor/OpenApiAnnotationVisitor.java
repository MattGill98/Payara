package fish.payara.microprofile.openapi.impl.visitor;

import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.Opcodes;

public abstract class OpenApiAnnotationVisitor extends AnnotationVisitor {

    protected final VisitorContext context;

    public OpenApiAnnotationVisitor(VisitorContext context) {
        super(Opcodes.ASM5);
        this.context = context;
    }

    @Override
    public void visitEnd() {
        context.clearAnnotationName();
        super.visitEnd();
    }
}