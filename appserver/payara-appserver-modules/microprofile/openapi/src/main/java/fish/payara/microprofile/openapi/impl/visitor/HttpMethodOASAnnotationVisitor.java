package fish.payara.microprofile.openapi.impl.visitor;

import java.util.logging.Level;
import java.util.logging.Logger;

import fish.payara.microprofile.openapi.impl.model.OperationImpl;

public class HttpMethodOASAnnotationVisitor extends OASAnnotationVisitor {

    private static final Logger LOGGER = Logger.getLogger(HttpMethodOASAnnotationVisitor.class.getName());

    public HttpMethodOASAnnotationVisitor(OASContext context) {
        super(context);
    }

    @Override
    public void visitEnd() {
        context.setWorkingOperation(new OperationImpl().operationId(context.getMethodName()), context.getAnnotationName());
        LOGGER.log(Level.INFO, "Path found: " + context.getPath());
    }

}