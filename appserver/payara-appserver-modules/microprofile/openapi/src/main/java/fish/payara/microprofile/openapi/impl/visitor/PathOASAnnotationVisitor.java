package fish.payara.microprofile.openapi.impl.visitor;

public class PathOASAnnotationVisitor extends OASAnnotationVisitor {

    private boolean method;

    public PathOASAnnotationVisitor(OASContext context, boolean method) {
        super(context);
        this.method = method;
    }

    @Override
    public void visit(String name, Object value) {
        if ("value".equals(name)) {
            context.addPathSegment((String) value, method);
        }
        super.visit(name, value);
    }

}