package fish.payara.microprofile.openapi.impl.visitor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpMethodOASAnnotationVisitor extends OASAnnotationVisitor {

    private static final Logger LOGGER = Logger.getLogger(HttpMethodOASAnnotationVisitor.class.getName());

    public HttpMethodOASAnnotationVisitor(OASContext context) {
        super(context);
    }

    @Override
    public void visitEnd() {
        context.setOperationMethod(context.getAnnotationName());
        LOGGER.log(Level.INFO, "Path found: " + context.getPath());
    }

}