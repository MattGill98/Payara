package fish.payara.microprofile.openapi.impl.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;

import fish.payara.microprofile.openapi.impl.model.responses.APIResponseImpl;

public class ProducesOASAnnotationVisitor extends OASAnnotationVisitor {

    private List<String> producesTypes = new ArrayList<>(3);

    public ProducesOASAnnotationVisitor(OASContext context) {
        super(context);
    }

    @Override
    public void visit(String name, Object value) {
        if (name == null && value != null && value.toString().isEmpty()) {
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

        // Make sure the @Produces wasn't empty, and check that no @APIResponses have been added
        if (producesTypes.isEmpty() && context.getCurrentOperation().getResponses().size() == 1) {
            for (String producesType : producesTypes) {
                context.getCurrentOperation().getResponses().addApiResponse(producesType, new APIResponseImpl());
            }
        }
        super.visitEnd();
    }

}