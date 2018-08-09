package fish.payara.microprofile.openapi.impl.visitor;


import java.util.logging.Logger;

import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;
import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;
import org.glassfish.pfl.objectweb.asm.Type;

import fish.payara.microprofile.openapi.impl.model.media.ContentImpl;
import fish.payara.microprofile.openapi.impl.model.responses.APIResponseImpl;
import fish.payara.microprofile.openapi.impl.model.responses.APIResponsesImpl;

public class APIResponseOASAnnotationVisitor extends OASAnnotationVisitor {

    private static final Logger LOGGER = Logger.getLogger(APIResponseOASAnnotationVisitor.class.getName());

    private APIResponses currentResponses;
    private APIResponse currentResponse;

    private String responseCode;

    public APIResponseOASAnnotationVisitor(OASContext context, APIResponses currentResponses) {
        super(context);
        this.currentResponse = new APIResponseImpl();
        this.currentResponses = currentResponses;
    }

    @Override
    public void visit(String name, Object value) {
        if (name != null) {
            switch (name) {
                case "responseCode":
                    responseCode = value.toString();
                    break;
                case "description":
                    currentResponse.setDescription(value.toString());
                    break;
                default:
                    LOGGER.info(String.format("Unrecognised property: '%s'.", name));
            }
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
            String className = Type.getType(desc).getClassName();
            switch (className) {
                case "org.eclipse.microprofile.openapi.annotations.responses.APIResponse":
                    return new APIResponseOASAnnotationVisitor(context, currentResponses);
                case "org.eclipse.microprofile.openapi.annotations.media.Content":
                    currentResponse.setContent(new ContentImpl());
                    return new ContentOASAnnotationVisitor(context, currentResponse.getContent());
                default:
                    LOGGER.info(String.format("Unrecognised annotation: '%s'.", className));
            }
        }
        return super.visitAnnotation(name, desc);
    }

    @Override
    public void visitEnd() {

        if (responseCode != null) {
            if (currentResponses.getDefault() != null) {
                currentResponses.remove(APIResponsesImpl.DEFAULT);
            }
            currentResponses.addApiResponse(responseCode, currentResponse);
        }

        super.visitEnd();
    }

}