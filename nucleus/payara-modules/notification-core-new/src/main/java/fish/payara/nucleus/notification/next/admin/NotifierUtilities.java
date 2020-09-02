package fish.payara.nucleus.notification.next.admin;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceHandle;

public final class NotifierUtilities {

    protected static String getNotifierName(final ServiceHandle<?> serviceHandle) {
        final ActiveDescriptor<?> descriptor = serviceHandle.getActiveDescriptor();
        String descriptorName = descriptor.getName();
        if (descriptorName == null || descriptorName.isEmpty()) {
            descriptorName = descriptor.getImplementationClass().getSimpleName();
        }
        return descriptorName;
    }
    
    private NotifierUtilities() {}
}