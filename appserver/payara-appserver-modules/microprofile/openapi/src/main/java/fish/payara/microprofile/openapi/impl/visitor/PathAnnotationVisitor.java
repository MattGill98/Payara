package fish.payara.microprofile.openapi.impl.visitor;

public class PathAnnotationVisitor extends OpenApiAnnotationVisitor {

    private final VisitorContext context;

    private boolean method;

    public PathAnnotationVisitor(VisitorContext context, boolean method) {
        super();
        this.context = context;
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