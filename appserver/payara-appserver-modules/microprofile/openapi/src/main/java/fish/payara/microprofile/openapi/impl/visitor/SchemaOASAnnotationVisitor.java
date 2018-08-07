package fish.payara.microprofile.openapi.impl.visitor;

import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;
import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;

public class SchemaOASAnnotationVisitor extends OASAnnotationVisitor {

    private Schema currentSchema;

    public SchemaOASAnnotationVisitor(OASContext context, Schema currentSchema) {
        super(context);
        this.currentSchema = currentSchema;
    }

    @Override
    public void visit(String name, Object value) {
        if ("description".equals(name)) {
            currentSchema.setDescription(value.toString());
        }
        super.visit(name, value);
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return super.visitArray(name);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        return super.visitAnnotation(name, desc);
    }

    @Override
    public void visitEnum(String name, String desc, String value) {
        if ("type".equals(name)) {
            currentSchema.setType(SchemaType.valueOf(value));
        }
        super.visitEnum(name, desc, value);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }

}