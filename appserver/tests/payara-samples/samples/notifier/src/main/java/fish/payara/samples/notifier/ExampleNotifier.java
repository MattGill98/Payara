package fish.payara.samples.notifier;

import org.jvnet.hk2.annotations.Service;

import fish.payara.notification.PayaraNotification;
import fish.payara.notification.PayaraNotifier;

@Service
public class ExampleNotifier implements PayaraNotifier {

    protected boolean initialised;
    protected boolean notificationReceived;

    @Override
    public Class<?> getConfigurationClass() {
        return null;
    }

    @Override
    public void handleNotification(PayaraNotification event) {
        notificationReceived = true;
    }

    @Override
    public void bootstrap() {
        initialised = true;
    }

    @Override
    public void destroy() {
        initialised = false;
    }

}