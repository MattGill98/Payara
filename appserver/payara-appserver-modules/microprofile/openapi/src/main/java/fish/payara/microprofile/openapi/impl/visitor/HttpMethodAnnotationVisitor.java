package fish.payara.microprofile.openapi.impl.visitor;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.microprofile.openapi.models.PathItem.HttpMethod;

import fish.payara.microprofile.openapi.impl.model.OperationImpl;

public class HttpMethodAnnotationVisitor extends OpenApiAnnotationVisitor {

    private static final Logger LOGGER = Logger.getLogger(HttpMethodAnnotationVisitor.class.getName());

    public HttpMethodAnnotationVisitor(VisitorContext context) {
        super(context);
    }

    @Override
    public void visitEnd() {


        HttpMethod method = null;
        switch (context.getAnnotationName()) {
            case "javax.ws.rs.GET":
                method = HttpMethod.GET; break;
            case "javax.ws.rs.POST":
                method = HttpMethod.POST; break;
            case "javax.ws.rs.PUT":
                method = HttpMethod.PUT; break;
            case "javax.ws.rs.DELETE":
                method = HttpMethod.DELETE; break;
            case "javax.ws.rs.PATCH":
                method = HttpMethod.PATCH; break;
            case "javax.ws.rs.OPTIONS":
                method = HttpMethod.OPTIONS; break;
            case "javax.ws.rs.HEAD":
                method = HttpMethod.HEAD; break;
            case "javax.ws.rs.TRACE":
                method = HttpMethod.TRACE; break;
        }

        context.setWorkingOperation(new OperationImpl().operationId(context.getMethodName()), method);
        LOGGER.log(Level.INFO, "Path found: " + context.getPath());
    }

}