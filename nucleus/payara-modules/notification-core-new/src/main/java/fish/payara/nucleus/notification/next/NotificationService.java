/*
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 Copyright (c) 2016 Payara Foundation. All rights reserved.
 The contents of this file are subject to the terms of the Common Development
 and Distribution License("CDDL") (collectively, the "License").  You
 may not use this file except in compliance with the License.  You can
 obtain a copy of the License at
 https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 or packager/legal/LICENSE.txt.  See the License for the specific
 language governing permissions and limitations under the License.
 When distributing the software, include this License Header Notice in each
 file and include the License file at packager/legal/LICENSE.txt.
 */
package fish.payara.nucleus.notification.next;

import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.sun.enterprise.config.serverbeans.Config;

import org.glassfish.api.StartupRunLevel;
import org.glassfish.api.admin.ServerEnvironment;
import org.glassfish.api.event.EventListener;
import org.glassfish.api.event.EventTypes;
import org.glassfish.api.event.Events;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.messaging.Topic;
import org.glassfish.hk2.runlevel.RunLevel;
import org.jvnet.hk2.annotations.Optional;
import org.jvnet.hk2.annotations.Service;
import org.jvnet.hk2.config.Changed;
import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.config.ConfigListener;
import org.jvnet.hk2.config.ConfigSupport;
import org.jvnet.hk2.config.NotProcessed;
import org.jvnet.hk2.config.Transactions;
import org.jvnet.hk2.config.UnprocessedChangeEvents;

import fish.payara.notification.PayaraNotifierConfiguration;
import fish.payara.nucleus.notification.next.configuration.NotificationServiceConfiguration;
import fish.payara.nucleus.notification.next.domain.NotificationEvent;

/**
 * Main service class that provides {@link #notify(NotificationEvent)} method used by services, which needs disseminating notifications.
 *
 * @author mertcaliskan
 */
@Service(name = "new-notification-service")
@RunLevel(StartupRunLevel.VAL)
public class NotificationService implements EventListener, ConfigListener {

    private static final Logger logger = Logger.getLogger(NotificationService.class.getCanonicalName());

    @Inject
    @Named(ServerEnvironment.DEFAULT_INSTANCE_NAME)
    @Optional
    private NotificationServiceConfiguration configuration;

    @Inject
    private Events events;

    @Inject
    private ServiceLocator habitat;

    @Inject
    private Transactions transactions;

    @Inject
    private ServerEnvironment env;
    
    @Inject
    private Topic<NotificationEvent> eventBus;

    @PostConstruct
    void postConstruct() {
        events.register(this);
        configuration = habitat.getService(NotificationServiceConfiguration.class);
    }

    public void event(Event event) {
        if (event.is(EventTypes.SERVER_READY)) {
            bootstrapNotificationService();
        }
        transactions.addListenerForType(NotificationServiceConfiguration.class, this);
    }

    public void bootstrapNotificationService() {
        if (configuration != null) {
            for (PayaraNotifierConfiguration notifierConfiguration : configuration.getNotifierConfigurationList()) {
                System.out.println(notifierConfiguration.getClass().getName());
            }
            if (Boolean.valueOf(configuration.getEnabled())) {
                logger.info("Payara Notification Service bootstrapped with configuration: " + configuration);
            }
        }
    }

    public void notify(NotificationEvent event) {
        eventBus.publish(event);
    }

    @Override
    public UnprocessedChangeEvents changed(PropertyChangeEvent[] events) {
        boolean isCurrentInstanceMatchTarget = false;
        if (env.isInstance()) {
            isCurrentInstanceMatchTarget = true;
        }
        else {
            for (PropertyChangeEvent pe : events) {
                ConfigBeanProxy proxy = (ConfigBeanProxy) pe.getSource();
                while (proxy != null && !(proxy instanceof Config)) {
                    proxy = proxy.getParent();
                }

                if (proxy != null && ((Config) proxy).isDas()) {
                    isCurrentInstanceMatchTarget = true;
                    break;
                }
            }
        }

        if (isCurrentInstanceMatchTarget) {
            return ConfigSupport.sortAndDispatch(events, new Changed() {

                @Override
                public <T extends ConfigBeanProxy> NotProcessed changed(TYPE type, Class<T> changedType, T changedInstance) {

                    if(changedType.equals(NotificationServiceConfiguration.class)) {
                        configuration = (NotificationServiceConfiguration) changedInstance;
                    }
                    return null;
                }
            }, logger);
        }
        return null;
    }
}