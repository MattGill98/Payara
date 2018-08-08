package fish.payara.microprofile.openapi.impl.visitor;

import java.util.logging.Logger;

public class ApplicationPathOASAnnotationVisitor extends OASAnnotationVisitor {

    private static final Logger LOGGER = Logger.getLogger(ApplicationPathOASAnnotationVisitor.class.getName());

    public ApplicationPathOASAnnotationVisitor(OASContext context) {
        super(context);
    }

    @Override
    public void visit(String name, Object value) {
        if (name != null) {
            switch (name) {
                case "value":
                    System.out.println("Application Path found: " + value);
                    context.setApplicationPath(value.toString());
                    break;
                default:
                    LOGGER.info(String.format("Unrecognised property: '%s'.", name));
            }
        }
        super.visit(name, value);
    }

}