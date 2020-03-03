package fish.payara.microprofile.openapi.impl.visitor;

import java.util.logging.Logger;

public class PathOASAnnotationVisitor extends OASAnnotationVisitor {

    private static final Logger LOGGER = Logger.getLogger(PathOASAnnotationVisitor.class.getName());

    private final boolean isMethodAnnotation;

    public PathOASAnnotationVisitor(OASContext context, boolean isMethodAnnotation) {
        super(context);
        this.isMethodAnnotation = isMethodAnnotation;
    }

    @Override
    public void visit(String name, Object value) {
        if (name != null) {
            switch (name) {
                case "value":
                    if (isMethodAnnotation) {
                        context.setResourcePath(value.toString());
                    } else {
                        context.setClassPath(value.toString());
                    }
                    break;
                default:
                    LOGGER.info(String.format("Unrecognised property: '%s'.", name));
            }
        }
        super.visit(name, value);
    }

}