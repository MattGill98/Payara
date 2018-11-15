package fish.payara.microprofile.openapi.impl.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.glassfish.api.deployment.archive.ReadableArchive;
import org.glassfish.hk2.external.org.objectweb.asm.ClassReader;
import org.glassfish.hk2.external.org.objectweb.asm.Opcodes;

import fish.payara.microprofile.openapi.api.processor.OASProcessor;
import fish.payara.microprofile.openapi.impl.config.OpenApiConfiguration;
import fish.payara.microprofile.openapi.impl.visitor.OASClassVisitor;
import fish.payara.microprofile.openapi.impl.visitor.OASContext;

public class ASMProcessor implements OASProcessor {

    public static final int ASM_VERSION = Opcodes.ASM6;

    private final Collection<InputStream> archive;

    public ASMProcessor(ReadableArchive archive) {
        this.archive = new HashSet<>();
        for (String entry : Collections.list(archive.entries())) {
            if (entry.endsWith(".class")) {
                try {
                    this.archive.add(archive.getEntry(entry));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ASMProcessor(Collection<InputStream> archive) {
        this.archive = archive;
    }

    @Override
    public OpenAPI process(OpenAPI api, OpenApiConfiguration config) {

        String applicationPath = null;
        OASContext context = new OASContext(api, applicationPath);

        for (InputStream entry : archive) {
            ClassReader reader = null;
            try {
                reader = new ClassReader(entry);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (reader != null) {
                reader.accept(new OASClassVisitor(context), ClassReader.SKIP_FRAMES);
                applicationPath = context.getApplicationPath();
            }
        }
        return api;
    }

}