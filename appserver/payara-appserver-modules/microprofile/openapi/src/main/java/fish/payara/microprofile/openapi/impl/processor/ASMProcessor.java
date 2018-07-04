package fish.payara.microprofile.openapi.impl.processor;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.glassfish.api.deployment.archive.ReadableArchive;
import org.glassfish.hk2.external.org.objectweb.asm.ClassReader;

import fish.payara.microprofile.openapi.api.processor.OASProcessor;
import fish.payara.microprofile.openapi.impl.config.OpenApiConfiguration;
import fish.payara.microprofile.openapi.impl.visitor.OpenApiClassVisitor;

public class ASMProcessor implements OASProcessor {

    private final ReadableArchive archive;

    public ASMProcessor(ReadableArchive archive) {
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
                    reader.accept(new OpenApiClassVisitor(),
                            ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                }
            }
        }
        return api;
    }

}