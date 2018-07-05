package fish.payara.microprofile.openapi.impl.visitor;

import java.util.Arrays;

import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;

import fish.payara.microprofile.openapi.impl.model.responses.APIResponseImpl;

public class ProducesOASAnnotationVisitor extends OASAnnotationVisitor {

    private String[] producesTypes;

    public ProducesOASAnnotationVisitor(OASContext context) {
        super(context);
    }

    @Override
    public void visit(String name, Object value) {
        if (name == null) {
            if (producesTypes == null) {
                producesTypes = new String[]{(String) value};
            } else {
                String[] copy = new String[producesTypes.length + 1];
                for (int i = 0; i < producesTypes.length; i++) {
                    copy[i] = producesTypes[i];
                }
                copy[producesTypes.length] = (String) value;
            }
        }
        super.visit(name, value);
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return this;
    }

    @Override
    public void visitEnd() {
        context.getCurrentOperation().getResponses().addApiResponse(producesTypes == null? null : producesTypes[0], new APIResponseImpl());
        super.visitEnd();
    }

}