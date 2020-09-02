package fish.payara.notification.example;

import org.jvnet.hk2.annotations.Service;

import fish.payara.notification.PayaraConfiguredNotifier;
import fish.payara.notification.PayaraNotification;

@Service
public class ExampleNotifier extends PayaraConfiguredNotifier<ExampleNotifierConfiguration> {

    @Override
    public void handleNotification(PayaraNotification event) {
        System.out.println(configuration.getTestValue());
    }

    @Override
    public void bootstrap() {
        System.out.println("Bootstrapping custom notifier");
    }

    @Override
    public void destroy() {
        System.out.println("Destroying custom notifier");
    }

}