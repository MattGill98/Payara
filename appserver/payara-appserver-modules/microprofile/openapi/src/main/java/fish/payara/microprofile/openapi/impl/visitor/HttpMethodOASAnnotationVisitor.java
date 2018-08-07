package fish.payara.microprofile.openapi.impl.visitor;

public class HttpMethodOASAnnotationVisitor extends OASAnnotationVisitor {

    private final String methodName;

    public HttpMethodOASAnnotationVisitor(OASContext context, String methodName) {
        super(context);
        this.methodName = methodName;
    }

    @Override
    public void visitEnd() {
    }

}