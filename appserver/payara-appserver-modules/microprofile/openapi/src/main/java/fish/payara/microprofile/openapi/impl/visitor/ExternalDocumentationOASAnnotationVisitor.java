package fish.payara.microprofile.openapi.impl.visitor;

import org.eclipse.microprofile.openapi.models.ExternalDocumentation;

public class ExternalDocumentationOASAnnotationVisitor extends OASAnnotationVisitor {

    private ExternalDocumentation currentDocumentation;

    public ExternalDocumentationOASAnnotationVisitor(OASContext context, ExternalDocumentation currentDocumentation) {
        super(context);
        this.currentDocumentation = currentDocumentation;
    }

    @Override
    public void visit(String name, Object value) {
        if ("url".equals(name)) {
            currentDocumentation.setUrl(value.toString());
        }
        if ("description".equals(name)) {
            currentDocumentation.setDescription(value.toString());
        }
        super.visit(name, value);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }

}