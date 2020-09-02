package fish.payara.notification;

import java.lang.reflect.ParameterizedType;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.glassfish.hk2.api.ServiceLocator;
import org.jvnet.hk2.annotations.Contract;

import fish.payara.notification.admin.NotificationServiceConfiguration;

@Contract
public abstract class PayaraConfiguredNotifier<NC extends PayaraNotifierConfiguration> implements PayaraNotifier {

    @Inject
    private ServiceLocator habitat;

    protected NC configuration;

    @PostConstruct
    void getConfiguration() {
        NotificationServiceConfiguration config = habitat.getService(NotificationServiceConfiguration.class);
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        Class<NC> configClass = (Class<NC>) genericSuperclass.getActualTypeArguments()[0];
        configuration = config.getNotifierConfigurationByType(configClass);
    }

}