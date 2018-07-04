package fish.payara.microprofile.openapi.impl.processor;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.glassfish.api.deployment.archive.ReadableArchive;
import org.glassfish.hk2.external.org.objectweb.asm.AnnotationVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.ClassReader;
import org.glassfish.hk2.external.org.objectweb.asm.ClassVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.FieldVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.MethodVisitor;
import org.glassfish.hk2.external.org.objectweb.asm.Opcodes;

import fish.payara.microprofile.openapi.api.processor.OASProcessor;
import fish.payara.microprofile.openapi.impl.config.OpenApiConfiguration;

public class ASMProcessor extends ClassVisitor implements OASProcessor {

    private final ReadableArchive archive;

    public ASMProcessor(ReadableArchive archive) {
        super(Opcodes.ASM5);
        this.archive = archive;
    }

    @Override
    public OpenAPI process(OpenAPI api, OpenApiConfiguration config) {
        for (String entry : Collections.list(archive.entries())) {
            if (entry.endsWith(".class")) {
                ClassReader reader = null;
                try {
                    reader = new ClassReader(archive.getEntry(entry));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (reader != null) {
                    reader.accept(this, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                }
            }
        }
        return api;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        System.out.println("Visiting class: " + name);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        System.out.println("Visiting annotation: " + desc);
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return new OpenApiFieldVisitor();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new OpenApiMethodVisitor();
    }

    private class OpenApiMethodVisitor extends MethodVisitor {

        private OpenApiMethodVisitor() {
            super(Opcodes.ASM5);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            System.out.println("Visiting annotation: " + desc);
            return super.visitAnnotation(desc, visible);
        }
    }

    private class OpenApiFieldVisitor extends FieldVisitor {

        private OpenApiFieldVisitor() {
            super(Opcodes.ASM5);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            System.out.println("Visiting annotation: " + desc);
            return super.visitAnnotation(desc, visible);
        }
    }

}