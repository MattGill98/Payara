package fish.payara.notification.example;

import java.beans.PropertyVetoException;

import org.jvnet.hk2.config.Attribute;
import org.jvnet.hk2.config.Configured;

import fish.payara.notification.PayaraNotifierConfiguration;

@Configured
public interface ExampleNotifierConfiguration extends PayaraNotifierConfiguration {

    @Attribute(defaultValue = "0", dataType = Integer.class)
    String getTestValue();
    void setTestValue(Integer value) throws PropertyVetoException;
    
}