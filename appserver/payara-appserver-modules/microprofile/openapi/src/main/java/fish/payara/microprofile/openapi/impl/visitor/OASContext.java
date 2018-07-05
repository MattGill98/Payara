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

import java.util.LinkedList;

import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.PathItem;

public class OASContext {

    private final OpenAPI openapi;

    // Variables for constructing the path
    private String applicationPath = "/";
    private String classPath;
    private String resourcePath;

    private String className;
    private String annotationName;

    private String operationMethod;
    private Operation currentOperation;

    public OASContext(OpenAPI openapi) {
        this(openapi, null);
    }

    public OASContext(OpenAPI openapi, String applicationPath) {
        this.openapi = openapi;
        this.classPath = "";
        this.resourcePath = "";
        this.applicationPath = applicationPath;
    }

    public OpenAPI getApi() {
        return openapi;
    }

    public String getPath() {
        return normaliseUrl(applicationPath, classPath, resourcePath);
    }

    public void addPathSegment(String path, boolean method) {
        if (method) {
            this.resourcePath = path;
        } else {
            this.classPath = path;
        }
    }

    public String getApplicationPath() {
        return applicationPath;
    }

    public void setApplicationPath(String applicationPath) {
        if (this.applicationPath == null) {
            this.applicationPath = applicationPath;
            for (String path : new LinkedList<>(openapi.getPaths().keySet())) {
                PathItem moved = openapi.getPaths().remove(path);
                openapi.getPaths().addPathItem(normaliseUrl(applicationPath, path), moved);
            }
        }
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public void setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
    }

    public void clearAnnotationName() {
        setAnnotationName(null);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void clearClassName() {
        setClassName(null);
    }

    public boolean isOperationValid() {
        return getOperationMethod() != null && getCurrentOperation() != null;
    }

    public String getOperationMethod() {
        return operationMethod;
    }

    public Operation getCurrentOperation() {
        return currentOperation;
    }

    public void setCurrentOperation(Operation currentOperation) {
        this.currentOperation = currentOperation;
    }

    public void setOperationMethod(String operationMethod) {
        this.operationMethod = operationMethod;
    }

    public void clearWorkingOperation() {
        setCurrentOperation(null);
        setOperationMethod(null);
    }

    public static String getClassName(String name) {
        if (name == null) {
            return "";
        }
        name = name.replace("/", ".");
        if (name.startsWith("L"))
            name = name.substring(1);
        if (name.endsWith(";"))
            name = name.substring(0, name.length() - 1);
        return name;
    }

    public static String normaliseUrl(String... urlComponents) {
        if (urlComponents == null || urlComponents.length == 0) {
            return "";
        }
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("/");

        if (urlComponents != null) {
            for (String component : urlComponents) {
                if (component != null) {
                    urlBuilder.append(component);
                }
                urlBuilder.append("/");
            }
        }

        // Remove trailing slash
        urlBuilder.deleteCharAt(urlBuilder.length() - 1);

        return urlBuilder.toString().replaceAll("/+", "/");
    }

}