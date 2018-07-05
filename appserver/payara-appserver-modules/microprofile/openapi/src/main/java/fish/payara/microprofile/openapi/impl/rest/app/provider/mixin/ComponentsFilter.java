package fish.payara.microprofile.openapi.impl.rest.app.provider.mixin;

import org.eclipse.microprofile.openapi.models.Components;

public class ComponentsFilter {

    /**
     * @return false if the components should be written, or true otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Components) {
            Components components = (Components) other;

            return components.getCallbacks().isEmpty() && components.getExamples().isEmpty()
                    && components.getExtensions().isEmpty() && components.getHeaders().isEmpty()
                    && components.getLinks().isEmpty() && components.getParameters().isEmpty()
                    && components.getParameters().isEmpty() && components.getRequestBodies().isEmpty()
                    && components.getResponses().isEmpty() && components.getSchemas().isEmpty()
                    && components.getSchemas().isEmpty() && components.getSecuritySchemes().isEmpty();
        }
        return false;
    }

}