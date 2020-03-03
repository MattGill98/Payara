package fish.payara.microprofile.openapi.impl.visitor;

import static fish.payara.microprofile.openapi.impl.visitor.OASContext.getClassName;

import org.eclipse.microprofile.openapi.models.PathItem;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fish.payara.microprofile.openapi.impl.model.PathItemImpl;

public final class OASMethodVisitor extends MethodVisitor {

    private final OASContext context;

    public OASMethodVisitor(OASContext context) {
        super(Opcodes.ASM5);
        this.context = context;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        context.setAnnotationName(getClassName(desc));

        if (desc != null) {
            switch (getClassName(desc)) {
                case "javax.ws.rs.Path":
                    return new PathOASAnnotationVisitor(context, true);
                case "javax.ws.rs.GET":
                case "javax.ws.rs.POST":
                case "javax.ws.rs.PUT":
                case "javax.ws.rs.DELETE":
                case "javax.ws.rs.PATCH":
                case "javax.ws.rs.OPTIONS":
                case "javax.ws.rs.HEAD":
                case "javax.ws.rs.TRACE":
                    return new HttpMethodOASAnnotationVisitor(context);
            }
        }

        return super.visitAnnotation(desc, visible);
    }

    @Override
    public void visitEnd() {
        if (context.getWorkingOperation() != null) {
            PathItem pathItem = context.getApi().getPaths().get(context.getPath());
            if (pathItem == null) {
                pathItem = new PathItemImpl();
                context.getApi().getPaths().addPathItem(context.getPath(), pathItem);
            }
            switch (context.getOperationMethod()) {
                case "javax.ws.rs.GET":
                    pathItem.GET(context.getWorkingOperation()); break;
                case "javax.ws.rs.POST":
                    pathItem.POST(context.getWorkingOperation()); break;
                case "javax.ws.rs.PUT":
                    pathItem.PUT(context.getWorkingOperation()); break;
                case "javax.ws.rs.DELETE":
                    pathItem.DELETE(context.getWorkingOperation()); break;
                case "javax.ws.rs.PATCH":
                    pathItem.PATCH(context.getWorkingOperation()); break;
                case "javax.ws.rs.OPTIONS":
                    pathItem.OPTIONS(context.getWorkingOperation()); break;
                case "javax.ws.rs.HEAD":
                    pathItem.HEAD(context.getWorkingOperation()); break;
                case "javax.ws.rs.TRACE":
                    pathItem.TRACE(context.getWorkingOperation()); break;
            }
        }

        context.clearWorkingOperation();
        context.clearMethodName();
        super.visitEnd();
    }
}