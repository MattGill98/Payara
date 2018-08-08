package fish.payara.microprofile.openapi.impl.visitor;

import java.util.logging.Logger;

public class PathOASAnnotationVisitor extends OASAnnotationVisitor {

    private static final Logger LOGGER = Logger.getLogger(PathOASAnnotationVisitor.class.getName());

    private final boolean method;

    public PathOASAnnotationVisitor(OASContext context, boolean method) {
        super(context);
        this.method = method;
    }

    @Override
    public void visit(String name, Object value) {
        if (name != null) {
            switch (name) {
                case "value":
                    context.addPathSegment((String) value, method);
                    break;
                default:
                    LOGGER.info(String.format("Unrecognised property: '%s'.", name));
            }
        }
        super.visit(name, value);
    }

}