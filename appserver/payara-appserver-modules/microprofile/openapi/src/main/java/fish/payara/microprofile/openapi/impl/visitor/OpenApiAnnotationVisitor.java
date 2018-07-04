package fish.payara.microprofile.openapi.impl.visitor;

import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.Opcodes;

public class OpenApiAnnotationVisitor extends AnnotationVisitor {
    public OpenApiAnnotationVisitor() {
        super(Opcodes.ASM5);
    }
}