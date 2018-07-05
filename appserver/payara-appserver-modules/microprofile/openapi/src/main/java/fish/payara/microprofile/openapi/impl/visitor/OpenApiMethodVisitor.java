package fish.payara.microprofile.openapi.impl.visitor;

import static fish.payara.microprofile.openapi.impl.visitor.VisitorContext.getClassName;

import org.eclipse.microprofile.openapi.models.PathItem;
import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.MethodVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.Opcodes;

import fish.payara.microprofile.openapi.impl.model.PathItemImpl;

public final class OpenApiMethodVisitor extends MethodVisitor {

    private final VisitorContext context;

    public OpenApiMethodVisitor(VisitorContext context) {
        super(Opcodes.ASM5);
        this.context = context;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        context.setAnnotationName(getClassName(desc));

        if (desc != null) {
            switch (getClassName(desc)) {
                case "javax.ws.rs.Path":
                    return new PathAnnotationVisitor(context, true);
                case "javax.ws.rs.GET":
                case "javax.ws.rs.POST":
                case "javax.ws.rs.PUT":
                case "javax.ws.rs.DELETE":
                case "javax.ws.rs.PATCH":
                case "javax.ws.rs.OPTIONS":
                case "javax.ws.rs.HEAD":
                case "javax.ws.rs.TRACE":
                    return new HttpMethodAnnotationVisitor(context);
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
                case GET:
                    pathItem.GET(context.getWorkingOperation()); break;
                case POST:
                    pathItem.POST(context.getWorkingOperation()); break;
                case PUT:
                    pathItem.PUT(context.getWorkingOperation()); break;
                case DELETE:
                    pathItem.DELETE(context.getWorkingOperation()); break;
                case PATCH:
                    pathItem.PATCH(context.getWorkingOperation()); break;
                case OPTIONS:
                    pathItem.OPTIONS(context.getWorkingOperation()); break;
                case HEAD:
                    pathItem.HEAD(context.getWorkingOperation()); break;
                case TRACE:
                    pathItem.TRACE(context.getWorkingOperation()); break;
            }
        }

        context.clearWorkingOperation();
        context.clearMethodName();
        super.visitEnd();
    }
}