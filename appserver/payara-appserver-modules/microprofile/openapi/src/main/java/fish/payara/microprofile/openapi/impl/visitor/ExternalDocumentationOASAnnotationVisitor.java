package fish.payara.microprofile.openapi.impl.visitor;

import java.util.logging.Logger;

import org.eclipse.microprofile.openapi.models.ExternalDocumentation;

public class ExternalDocumentationOASAnnotationVisitor extends OASAnnotationVisitor {

    private static final Logger LOGGER = Logger.getLogger(ExternalDocumentationOASAnnotationVisitor.class.getName());

    private ExternalDocumentation currentDocumentation;

    public ExternalDocumentationOASAnnotationVisitor(OASContext context, ExternalDocumentation currentDocumentation) {
        super(context);
        this.currentDocumentation = currentDocumentation;
    }

    @Override
    public void visit(String name, Object value) {
        if (name != null) {
            switch (name) {
                case "url":
                    currentDocumentation.setUrl(value.toString());
                    break;
                case "description":
                    currentDocumentation.setDescription(value.toString());
                    break;
                default:
                    LOGGER.info(String.format("Unrecognised property: '%s'.", name));
            }
        }
        super.visit(name, value);
    }

}