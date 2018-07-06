package fish.payara.microprofile.openapi.impl.visitor;

public class HttpMethodOASAnnotationVisitor extends OASAnnotationVisitor {

    public HttpMethodOASAnnotationVisitor(OASContext context) {
        super(context);
    }

    @Override
    public void visitEnd() {
        context.setOperationMethod(context.getAnnotationName());
    }

}