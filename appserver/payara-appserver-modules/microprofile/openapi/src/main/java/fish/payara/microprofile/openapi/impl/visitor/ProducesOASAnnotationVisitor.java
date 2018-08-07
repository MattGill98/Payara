package fish.payara.microprofile.openapi.impl.visitor;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;
import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;

public class ProducesOASAnnotationVisitor extends OASAnnotationVisitor {

    private APIResponses currentResponses;

    private List<String> producesTypes;

    public ProducesOASAnnotationVisitor(OASContext context, APIResponses currentResponses) {
        super(context);
        this.producesTypes = new ArrayList<>(3);
        this.currentResponses = currentResponses;
    }

    @Override
    public void visit(String name, Object value) {
        if (name == null && value != null && !value.toString().isEmpty()) {
            producesTypes.add((String) value);
        }
        super.visit(name, value);
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return this;
    }

    @Override
    public void visitEnd() {

        // If @Produce types have been added
        if (!producesTypes.isEmpty()) {
            // Find the default response
            APIResponse defaultResponse = currentResponses.getDefault();
            // If it exists, and there is a wildcard content
            if (defaultResponse != null && defaultResponse.getContent().get(MediaType.WILDCARD) != null) {
                // Copy the wildcard content to all the produce types, and remove the wildcard
                for (String producesType : producesTypes) {
                    defaultResponse.getContent().addMediaType(producesType,
                            defaultResponse.getContent().get(MediaType.WILDCARD));
                }
                defaultResponse.getContent().remove(MediaType.WILDCARD);
            }
        }
        super.visitEnd();
    }

}