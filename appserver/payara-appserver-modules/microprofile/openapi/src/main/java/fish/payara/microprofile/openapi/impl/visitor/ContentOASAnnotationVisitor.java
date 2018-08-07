package fish.payara.microprofile.openapi.impl.visitor;

import static fish.payara.microprofile.openapi.impl.visitor.OASContext.getClassName;

import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;

public class ContentOASAnnotationVisitor extends OASAnnotationVisitor {

    private String mediaType;

    public ContentOASAnnotationVisitor(OASContext context) {
        super(context);
    }

    @Override
    public void visit(String name, Object value) {
        if ("mediaType".equals(name)) {
            mediaType = value.toString();
        }
        super.visit(name, value);
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return this;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        if (desc != null) {
            String className = getClassName(desc);
            switch (className) {
            }
        }
        return super.visitAnnotation(name, desc);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }

}