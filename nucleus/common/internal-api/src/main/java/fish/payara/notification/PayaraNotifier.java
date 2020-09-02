package fish.payara.notification;

import org.glassfish.hk2.api.messaging.SubscribeTo;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface PayaraNotifier {

    void handleNotification(@SubscribeTo PayaraNotification event);

    default void bootstrap() {};

    default void destroy() {};

}