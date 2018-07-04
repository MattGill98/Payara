package fish.payara.microprofile.openapi.impl.visitor;

import org.eclipse.microprofile.openapi.models.OpenAPI;

public class PathAnnotationVisitor extends OpenApiAnnotationVisitor {

    private final OpenAPI openapi;

    public PathAnnotationVisitor(OpenAPI openapi) {
        super();
        this.openapi = openapi;
    }

    @Override
    public void visit(String name, Object value) {
        if ("value".equals(name)) {
            System.out.println("Path found: " + value);
        }
        super.visit(name, value);
    }

}