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
package fish.payara.microprofile.openapi.impl.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toSet;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.OASModelReader;
import org.glassfish.hk2.classmodel.reflect.Type;

public class OpenApiConfiguration {

    private static final Logger LOGGER = Logger.getLogger(OpenApiConfiguration.class.getName());

    private static final String MODEL_READER_KEY = "mp.openapi.model.reader";
    private static final String FILTER_KEY = "mp.openapi.filter";
    private static final String SCAN_LIB_KEY = "mp.openapi.scan.lib";
    private static final String SCAN_DISABLE_KEY = "mp.openapi.scan.disable";
    private static final String SCAN_PACKAGES_KEY = "mp.openapi.scan.packages";
    private static final String SCAN_CLASSES_KEY = "mp.openapi.scan.classes";
    private static final String SCAN_EXCLUDE_PACKAGES_KEY = "mp.openapi.scan.exclude.packages";
    private static final String SCAN_EXCLUDE_CLASSES_KEY = "mp.openapi.scan.exclude.classes";
    private static final String SERVERS_KEY = "mp.openapi.servers";
    private static final String PATH_PREFIX_KEY = "mp.openapi.servers.path.";
    private static final String OPERATION_PREFIX_KEY = "mp.openapi.servers.operation.";

    private Class<? extends OASModelReader> modelReader;
    private Class<? extends OASFilter> filter;
    private boolean scanDisable;
    private boolean scanLib;
    private List<String> scanPackages = new ArrayList<>();
    private List<String> scanClasses = new ArrayList<>();
    private List<String> scanExcludePackages = new ArrayList<>();
    private List<String> scanExcludeClasses = new ArrayList<>();
    private List<String> servers = new ArrayList<>();
    private Map<String, Set<String>> pathServerMap = new HashMap<>();
    private Map<String, Set<String>> operationServerMap = new HashMap<>();

    public OpenApiConfiguration(ClassLoader applicationClassLoader) {
        // Find the correct configuration instance
        if (applicationClassLoader == null) {
            applicationClassLoader = getClass().getClassLoader();
        }
        Config config = ConfigProvider.getConfig(applicationClassLoader);

        // Find each variable
        for (String propertyName : config.getPropertyNames()) {
            parseModelReader(propertyName, config);
            parseFilter(propertyName, config);
            parseScanDisable(propertyName, config);
            parseScanLib(propertyName, config);
            parseScanPackages(propertyName, config);
            parseScanClasses(propertyName, config);
            parseExcludePackages(propertyName, config);
            parseExcludeClasses(propertyName, config);
            parseServers(propertyName, config);
            parsePathServer(propertyName, config);
            parseOperationServer(propertyName, config);
        }
    }

    /**
     * @return the {@link OASModelReader} class provided by the application.
     */
    public Class<? extends OASModelReader> getModelReader() {
        return modelReader;
    }

    /**
     * @return the {@link OASFilter} class provided by the application.
     */
    public Class<? extends OASFilter> getFilter() {
        return filter;
    }

    /**
     * @return whether to disable application scanning.
     */
    public boolean getScanDisable() {
        return scanDisable;
    }

    /**
     * @return whether to disable packaged libraries scanning.
     */
    public boolean getScanLib() {
        return scanLib;
    }

    /**
     * @return a whitelist of packages to scan in the application.
     */
    public List<String> getScanPackages() {
        return scanPackages;
    }

    /**
     * @return a whitelist of classes to scan in the application.
     */
    public List<String> getScanClasses() {
        return scanClasses;
    }

    /**
     * @return a blacklist of packages to not scan in the application.
     */
    public List<String> getExcludePackages() {
        return scanExcludePackages;
    }

    /**
     * @return a blacklist of classes to not scan in the application.
     */
    public List<String> getExcludeClasses() {
        return scanExcludeClasses;
    }

    /**
     * @return a list of servers to add to the root document.
     */
    public List<String> getServers() {
        return servers;
    }

    /**
     * @return a map of paths to the servers it contains.
     */
    public Map<String, Set<String>> getPathServerMap() {
        return pathServerMap;
    }

    /**
     * @return a map of operation ids to the servers it contains.
     */
    public Map<String, Set<String>> getOperationServerMap() {
        return operationServerMap;
    }

    private void parseModelReader(String propertyName, Config config) {
        if (propertyName.equals(MODEL_READER_KEY)) {
            this.modelReader = parseClass(propertyName, config, "Model Reader", OASModelReader.class);
        }
    }

    private void parseFilter(String propertyName, Config config) {
        if (propertyName.equals(FILTER_KEY)) {
            this.filter = parseClass(propertyName, config, "Filter", OASFilter.class);
        }
    }

    private void parseScanDisable(String propertyName, Config config) {
        if (propertyName.equals(SCAN_DISABLE_KEY)) {
            this.scanDisable = config.getValue(propertyName, Boolean.class);
        }
    }

    private void parseScanLib(String propertyName, Config config) {
        if (propertyName.equals(SCAN_LIB_KEY)) {
            this.scanLib = config.getValue(propertyName, Boolean.class);
        }
    }

    private void parseScanPackages(String propertyName, Config config) {
        if (propertyName.equals(SCAN_PACKAGES_KEY)) {
            this.scanPackages = parseList(propertyName, config);
        }
    }

    private void parseScanClasses(String propertyName, Config config) {
        if (propertyName.equals(SCAN_CLASSES_KEY)) {
            this.scanClasses = parseList(propertyName, config);
        }
    }

    private void parseExcludePackages(String propertyName, Config config) {
        if (propertyName.equals(SCAN_EXCLUDE_PACKAGES_KEY)) {
            this.scanExcludePackages = parseList(propertyName, config);
        }
    }

    private void parseExcludeClasses(String propertyName, Config config) {
        if (propertyName.equals(SCAN_EXCLUDE_CLASSES_KEY)) {
            this.scanExcludeClasses = parseList(propertyName, config);
        }
    }

    private void parseServers(String propertyName, Config config) {
        if (propertyName.equals(SERVERS_KEY)) {
            this.servers = parseList(propertyName, config);
        }
    }

    private void parsePathServer(String propertyName, Config config) {
        if (propertyName.startsWith(PATH_PREFIX_KEY)) {
            final String pathServer = propertyName.replaceFirst(PATH_PREFIX_KEY, "");
            pathServerMap.put(pathServer, parseSet(propertyName, config));
        }
    }

    private void parseOperationServer(String propertyName, Config config) {
        if (propertyName.startsWith(OPERATION_PREFIX_KEY)) {
            final String operationServer = propertyName.replaceFirst(OPERATION_PREFIX_KEY, "");
            operationServerMap.put(operationServer, parseSet(propertyName, config));
        }
    }

    /**
     * @param types the list of classes to filter.
     * @return a filtered list of classes, using {@link #getScanClasses()},
     *         {@link #getExcludeClasses()}, {@link #getScanPackages()} and
     *         {@link #getExcludePackages()}.
     */
    public Set<Type> getValidClasses(Collection<Type> types) {
        return types.stream()
                // If scan classes are specified, check that the class is in the list
                .filter(type -> scanClasses.isEmpty() || scanClasses.contains(type.getName()))
                // If exclude classes are specified, check that the class is not the list
                .filter(type -> scanExcludeClasses.isEmpty() || !scanExcludeClasses.contains(type.getName()))
                // If scan packages are specified, check that the class package starts with one
                // in the list
                .filter(type -> scanPackages.isEmpty()
                        || scanPackages.stream().anyMatch(pkg -> type.getName().startsWith(pkg)))
                // If exclude packages are specified, check that the class package doesn't start
                // with any in the list
                .filter(clazz -> scanExcludePackages.isEmpty()
                        || scanExcludePackages.stream().noneMatch(pkg -> clazz.getName().startsWith(pkg)))
                .collect(toSet());
    }

    // static Config API convenience methods

    @SuppressWarnings("unchecked")
    private static <T> Class<T> parseClass(String propertyName, Config config, String className, Class<T> clazz) {
        try {
            return (Class<T>) config.getValue(propertyName, Class.class);
        } catch (NoSuchElementException ex) {
            LOGGER.warning(className + " class not found: " + config.getValue(propertyName, String.class));
        } catch (ClassCastException ex) {
            LOGGER.log(Level.WARNING, className + " class was wrong type: " + config.getValue(propertyName, String.class), ex);
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Failed to read " + className + " class" + config.getValue(propertyName, String.class), ex);
        }
        return null;
    }

    private static List<String> parseList(String propertyName, Config config) {
        return parseCollection(propertyName, config, new ArrayList<>());
    }

    private static Set<String> parseSet(String propertyName, Config config) {
        return parseCollection(propertyName, config, new HashSet<>());
    }

    private static <C extends Collection<String>> C parseCollection(String propertyName, Config config, C instance) {
        final String[] array = config.getValue(propertyName, String[].class);
        if (array != null) {
            for (String item : array) {
                instance.add(item);
            }
        }
        return instance;
    }

}
