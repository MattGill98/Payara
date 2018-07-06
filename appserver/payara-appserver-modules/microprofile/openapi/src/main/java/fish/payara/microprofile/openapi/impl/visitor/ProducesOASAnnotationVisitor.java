package fish.payara.microprofile.openapi.impl.visitor;

import java.util.ArrayList;
import java.util.List;

import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;

import fish.payara.microprofile.openapi.impl.model.responses.APIResponseImpl;

public class ProducesOASAnnotationVisitor extends OASAnnotationVisitor {

    private List<String> producesTypes = new ArrayList<>();

    public ProducesOASAnnotationVisitor(OASContext context) {
        super(context);
    }

    @Override
    public void visit(String name, Object value) {
        if (name == null && value != null) {
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
        context.getCurrentOperation().getResponses().addApiResponse(producesTypes.isEmpty() ? null : producesTypes.get(0),
                new APIResponseImpl());
        super.visitEnd();
    }

}