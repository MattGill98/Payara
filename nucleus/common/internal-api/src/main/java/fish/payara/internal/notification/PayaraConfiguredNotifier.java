/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) [2020] Payara Foundation and/or its affiliates. All rights reserved.
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
package fish.payara.internal.notification;

import java.lang.reflect.ParameterizedType;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.glassfish.config.support.GlassFishStubBean;
import org.glassfish.hk2.api.ServiceLocator;
import org.jvnet.hk2.annotations.Contract;

import fish.payara.internal.notification.admin.NotificationServiceConfiguration;

/**
 * A notifier that is backed by a configuration in the domain.xml
 * 
 * @param <NC> the configuration class for the notifier
 */
@Contract
public abstract class PayaraConfiguredNotifier<NC extends PayaraNotifierConfiguration> implements PayaraNotifier {

    private final Class<NC> configClass;

    @Inject
    private ServiceLocator habitat;

    private NC domainConfiguration;

    protected NC configuration;

    public PayaraConfiguredNotifier() {
        this.configClass = getConfigurationClass(getClass());
    }

    @PostConstruct
    void injectConfiguration() {
        final NotificationServiceConfiguration config = habitat.getService(NotificationServiceConfiguration.class);
        this.domainConfiguration = config.getNotifierConfigurationByType(getConfigurationClass(getClass()));
        this.configuration = GlassFishStubBean.cloneBean(domainConfiguration, configClass);
    }

    /**
     * Bootstrap the notifier and update the configuration from the domain.xml.
     */
    @Override
    public void bootstrap() {
        this.configuration = GlassFishStubBean.cloneBean(domainConfiguration, configClass);
    }

    public NC getConfiguration() {
        return configuration;
    }

    /**
     * @param <NC>          a generic class of the notifier configuration class
     * @param notifierClass the notifier of the class
     * @return the class used to configure the configured notifier
     */
    public static <NC extends PayaraNotifierConfiguration> Class<NC> getConfigurationClass(
            Class<?> notifierClass) {
        final ParameterizedType genericSuperclass = (ParameterizedType) notifierClass.getGenericSuperclass();
        return (Class<NC>) genericSuperclass.getActualTypeArguments()[0];
    }

}