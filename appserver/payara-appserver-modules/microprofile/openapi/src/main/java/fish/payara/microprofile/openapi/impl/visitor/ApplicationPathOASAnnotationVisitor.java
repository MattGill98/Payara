package fish.payara.microprofile.openapi.impl.visitor;

public class ApplicationPathOASAnnotationVisitor extends OASAnnotationVisitor {

    public ApplicationPathOASAnnotationVisitor(OASContext context) {
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