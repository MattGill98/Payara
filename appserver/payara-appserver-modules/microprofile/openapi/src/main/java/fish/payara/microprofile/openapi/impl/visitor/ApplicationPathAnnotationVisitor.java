package fish.payara.microprofile.openapi.impl.visitor;

public class ApplicationPathAnnotationVisitor extends OpenApiAnnotationVisitor {

    public ApplicationPathAnnotationVisitor(VisitorContext context) {
        super(context);
    }

    @Override
    public void visit(String name, Object value) {
        if ("value".equals(name)) {
            System.out.println("Application Path found: " + value);
            context.setApplicationPath((String) value);
        }
        super.visit(name, value);
    }

}