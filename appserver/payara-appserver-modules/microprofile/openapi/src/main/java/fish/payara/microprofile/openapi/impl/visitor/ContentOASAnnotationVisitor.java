package fish.payara.microprofile.openapi.impl.visitor;

import static fish.payara.microprofile.openapi.impl.visitor.OASContext.getClassName;

import org.eclipse.microprofile.openapi.models.media.Content;
import org.eclipse.microprofile.openapi.models.media.MediaType;
import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;

import fish.payara.microprofile.openapi.impl.model.media.MediaTypeImpl;
import fish.payara.microprofile.openapi.impl.model.media.SchemaImpl;

public class ContentOASAnnotationVisitor extends OASAnnotationVisitor {

    private Content currentContent;
    private MediaType currentMediaType;

    private String mediaType;

    public ContentOASAnnotationVisitor(OASContext context, Content currentContent) {
        super(context);
        this.currentContent = currentContent;
        this.currentMediaType = new MediaTypeImpl();
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
                case "org.eclipse.microprofile.openapi.annotations.media.Schema":
                    currentMediaType.setSchema(new SchemaImpl());
                    return new SchemaOASAnnotationVisitor(context, currentMediaType.getSchema());
            }
        }
        return super.visitAnnotation(name, desc);
    }

    @Override
    public void visitEnd() {

        if (mediaType != null) {
            currentContent.addMediaType(mediaType, currentMediaType);
        }

        super.visitEnd();
    }

}