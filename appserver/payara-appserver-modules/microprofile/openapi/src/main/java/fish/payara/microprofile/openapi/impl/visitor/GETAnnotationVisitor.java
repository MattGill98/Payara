package fish.payara.microprofile.openapi.impl.visitor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GETAnnotationVisitor extends OpenApiAnnotationVisitor {

    private static final Logger LOGGER = Logger.getLogger(GETAnnotationVisitor.class.getName());

    private final VisitorContext context;

    public GETAnnotationVisitor(VisitorContext context) {
        super();
        this.context = context;
    }

    @Override
    public void visitEnd() {
        LOGGER.log(Level.INFO, "Path found: " + context.getPath());
    }

}