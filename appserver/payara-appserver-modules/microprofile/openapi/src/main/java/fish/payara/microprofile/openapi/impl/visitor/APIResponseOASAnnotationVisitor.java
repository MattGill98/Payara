package fish.payara.microprofile.openapi.impl.visitor;

import static fish.payara.microprofile.openapi.impl.visitor.OASContext.getClassName;

import org.eclipse.microprofile.openapi.models.responses.APIResponses;
import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;

import fish.payara.microprofile.openapi.impl.model.responses.APIResponseImpl;

public class APIResponseOASAnnotationVisitor extends OASAnnotationVisitor {

    private APIResponses currentResponses;

    private String responseCode;

    public APIResponseOASAnnotationVisitor(OASContext context, APIResponses currentResponses) {
        super(context);
        this.currentResponses = currentResponses;
    }

    @Override
    public void visit(String name, Object value) {
        if ("responseCode".equals(name)) {
            responseCode = value.toString();
        }
        super.visit(name, value);
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return this;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        if (desc != null) {
            String className = getClassName(desc);
            switch (className) {
                case "org.eclipse.microprofile.openapi.annotations.responses.APIResponse":
                    return new APIResponseOASAnnotationVisitor(context, currentResponses);
                case "org.eclipse.microprofile.openapi.annotations.media.Content":
                    return new ContentOASAnnotationVisitor(context);
            }
        }
        return super.visitAnnotation(name, desc);
    }

    @Override
    public void visitEnd() {

        if (responseCode != null) {
            currentResponses.addApiResponse(responseCode, new APIResponseImpl());
        }

        super.visitEnd();
    }

}