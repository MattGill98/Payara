package fish.payara.notification;

public interface PayaraNotification {

    String getEventType();

    String getServerName();

    String getHostName();

    String getDomainName();

    String getInstanceName();

    String getSubject();

    String getMessage();
}
