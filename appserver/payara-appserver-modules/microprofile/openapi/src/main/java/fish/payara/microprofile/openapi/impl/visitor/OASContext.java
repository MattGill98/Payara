/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) [2018] Payara Foundation and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://github.com/payara/Payara/blob/master/LICENSE.txt
 * See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * The Payara Foundation designates this particular file as subject to the "Classpath"
 * exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package fish.payara.microprofile.openapi.impl.visitor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.PathItem;

import fish.payara.microprofile.openapi.impl.model.media.SchemaImpl;

public class OASContext {

    private final OpenAPI openapi;

    // Variables for constructing the path
    private String applicationPath;
    private String classPath;
    private String resourcePath;

    private Map<String, SchemaImpl> schemaMap;

    public OASContext(OpenAPI openapi) {
        this.openapi = openapi;
        this.classPath = null;
        this.resourcePath = null;
        this.applicationPath = "/";
        this.schemaMap = new HashMap<>();
    }

    /**
     * @return the OpenAPI document currently being built.
     */
    public OpenAPI getApi() {
        return openapi;
    }

    /**
     * @return the path of the current method.
     */
    public String getPath() {
        return normaliseUrl(applicationPath, classPath, resourcePath);
    }

    public void setApplicationPath(String applicationPath) {
        this.applicationPath = applicationPath;
        for (String path : new LinkedList<>(openapi.getPaths().keySet())) {
            PathItem moved = openapi.getPaths().getPathItem(path);
            openapi.getPaths().removePathItem(path);
            openapi.getPaths().addPathItem(normaliseUrl(applicationPath, path), moved);
        }
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public boolean isOperationValid() {
        return classPath != null;
    }

    public boolean containsSchema(String className) {
        return schemaMap.containsKey(className);
    }

    public SchemaImpl getSchema(String className) {
        return schemaMap.get(className);
    }

    public boolean addSchema(String className, SchemaImpl schema) {
        if (schemaMap.containsKey(className)) {
            return false;
        }
        schemaMap.put(className, schema);
        return true;
    }

    // STATIC METHODS

    public static String normaliseUrl(String... urlComponents) {
        if (urlComponents == null || urlComponents.length == 0) {
            return "";
        }
        // Start with two slashes
        String normalised = "//";

        if (urlComponents != null) {
            // For each urlComponent, add the part followed by a slash
            for (String component : urlComponents) {
                if (component != null) {
                    normalised = normalised + component + "/";
                }
            }
        }

        // Remove duplicate slashes
        normalised = normalised.replaceAll("/+", "/");

        // Remove trailing slash
        if (!"/".equals(normalised)) {
            normalised = normalised.substring(0, normalised.length() - 1);
        }

        return normalised;
    }

    public static String getSimpleName(String className) {
        if (className == null) {
            return null;
        }
        Matcher simpleNameMatcher = Pattern.compile(".+[$\\.](.+)").matcher(className);
        if (simpleNameMatcher.matches()) {
            return simpleNameMatcher.group(1);
        }
        return className;
    }

}