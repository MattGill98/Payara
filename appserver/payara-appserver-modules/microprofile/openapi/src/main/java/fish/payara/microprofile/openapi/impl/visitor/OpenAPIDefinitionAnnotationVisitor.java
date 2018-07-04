package fish.payara.microprofile.openapi.impl.visitor;

import org.eclipse.microprofile.openapi.models.OpenAPI;

public class OpenAPIDefinitionAnnotationVisitor extends OpenApiAnnotationVisitor {

    private final OpenAPI openapi;

    public OpenAPIDefinitionAnnotationVisitor(OpenAPI openapi) {
        super();
        this.openapi = openapi;
    }

}