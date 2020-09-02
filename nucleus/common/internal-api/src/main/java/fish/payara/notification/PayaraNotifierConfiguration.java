package fish.payara.notification;

import java.beans.PropertyVetoException;

import org.jvnet.hk2.config.Attribute;
import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.config.Configured;

@Configured
public interface PayaraNotifierConfiguration extends ConfigBeanProxy {

    @Attribute(defaultValue = "false", dataType = Boolean.class)
    String getEnabled();
    void enabled(Boolean value) throws PropertyVetoException;
    
    @Attribute(defaultValue = "true", dataType = Boolean.class)
    String getNoisy();
    void noisy(Boolean value) throws PropertyVetoException;

}