package fish.payara.microprofile.openapi.impl.visitor;

import java.math.BigDecimal;
import static fish.payara.microprofile.openapi.impl.visitor.OASContext.getClassName;
import java.util.logging.Logger;

import org.eclipse.microprofile.openapi.models.ExternalDocumentation;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;
import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;

import fish.payara.microprofile.openapi.impl.model.ExternalDocumentationImpl;

public class SchemaOASAnnotationVisitor extends OASAnnotationVisitor {

    private static final Logger LOGGER = Logger.getLogger(SchemaOASAnnotationVisitor.class.getName());

    private final Schema currentSchema;

    private String arrayName;

    private ExternalDocumentation externalDocs;

    public SchemaOASAnnotationVisitor(OASContext context, Schema currentSchema) {
        super(context);
        this.currentSchema = currentSchema;
    }

    public SchemaOASAnnotationVisitor(OASContext context, Schema currentSchema, String arrayName) {
        this(context, currentSchema);
        this.arrayName = arrayName;
    }

    @Override
    public void visit(String name, Object value) {
        if (arrayName != null) {
            name = arrayName;
        } if (name != null) {
            switch (name) {
                case "description":
                    currentSchema.setDescription(value.toString());
                    break;
                case "title":
                    currentSchema.setTitle(value.toString());
                    break;
                case "multipleOf":
                    currentSchema.setMultipleOf(new BigDecimal(value.toString()));
                    break;
                case "maximum":
                    currentSchema.setMaximum(new BigDecimal(value.toString()));
                    break;
                case "minimum":
                    currentSchema.setMinimum(new BigDecimal(value.toString()));
                    break;
                case "exclusiveMaximum":
                    currentSchema.setExclusiveMaximum(Boolean.valueOf(value.toString()));
                    break;
                case "exclusiveMinimum":
                    currentSchema.setExclusiveMinimum(Boolean.valueOf(value.toString()));
                    break;
                case "maxLength":
                    currentSchema.setMaxLength(Integer.valueOf(value.toString()));
                    break;
                case "minLength":
                    currentSchema.setMinLength(Integer.valueOf(value.toString()));
                    break;
                case "maxProperties":
                    currentSchema.setMaxProperties(Integer.valueOf(value.toString()));
                    break;
                case "minProperties":
                    currentSchema.setMinProperties(Integer.valueOf(value.toString()));
                    break;
                case "maxItems":
                    currentSchema.setMaxItems(Integer.valueOf(value.toString()));
                    break;
                case "minItems":
                    currentSchema.setMinItems(Integer.valueOf(value.toString()));
                    break;
                case "pattern":
                    currentSchema.setPattern(value.toString());
                    break;
                case "format":
                    currentSchema.setFormat(value.toString());
                    break;
                case "nullable":
                    currentSchema.setNullable(Boolean.valueOf(value.toString()));
                    break;
                case "readOnly":
                    currentSchema.setReadOnly(Boolean.valueOf(value.toString()));
                    break;
                case "writeOnly":
                    currentSchema.setWriteOnly(Boolean.valueOf(value.toString()));
                    break;
                case "example":
                    currentSchema.setExample(value);
                    break;
                case "defaultValue":
                    currentSchema.setDefaultValue(value);
                    break;
                case "deprecated":
                    currentSchema.setDeprecated(Boolean.valueOf(value.toString()));
                    break;
                case "requiredProperties":
                    currentSchema.addRequired(value.toString());
                    break;
                case "enumeration":
                    currentSchema.addEnumeration(value);
                    break;
                default:
                    LOGGER.info(String.format("Unrecognised property: '%s'.", name));
            }
        }
        super.visit(name, value);
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return new SchemaOASAnnotationVisitor(context, currentSchema, name);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        if (desc != null) {
            String className = getClassName(desc);
            switch (className) {
                case "org.eclipse.microprofile.openapi.annotations.ExternalDocumentation":
                    externalDocs = new ExternalDocumentationImpl();
                    return new ExternalDocumentationOASAnnotationVisitor(context, externalDocs);
            }
        }
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
        if (externalDocs != null) {
            currentSchema.setExternalDocs(externalDocs);
        }
        super.visitEnd();
    }

}