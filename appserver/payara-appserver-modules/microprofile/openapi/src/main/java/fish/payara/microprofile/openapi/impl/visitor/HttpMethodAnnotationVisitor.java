package fish.payara.microprofile.openapi.impl.visitor;

import java.util.logging.Level;
import java.util.logging.Logger;

import fish.payara.microprofile.openapi.impl.model.OperationImpl;

public class HttpMethodAnnotationVisitor extends OpenApiAnnotationVisitor {

    private static final Logger LOGGER = Logger.getLogger(HttpMethodAnnotationVisitor.class.getName());

    private final VisitorContext context;

    public HttpMethodAnnotationVisitor(VisitorContext context) {
        super();
        this.context = context;
    }

    @Override
    public void visitEnd() {
        context.setWorkingOperation(new OperationImpl().operationId(OpenApiClassVisitor.METHOD_NAME));
        LOGGER.log(Level.INFO, "Path found: " + context.getPath());
    }

}