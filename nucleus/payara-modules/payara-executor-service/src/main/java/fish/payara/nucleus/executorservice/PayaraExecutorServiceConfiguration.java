/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 *    Copyright (c) [2017] Payara Foundation and/or its affiliates. All rights reserved.
 * 
 *     The contents of this file are subject to the terms of either the GNU
 *     General Public License Version 2 only ("GPL") or the Common Development
 *     and Distribution License("CDDL") (collectively, the "License").  You
 *     may not use this file except in compliance with the License.  You can
 *     obtain a copy of the License at
 *     https://github.com/payara/Payara/blob/master/LICENSE.txt
 *     See the License for the specific
 *     language governing permissions and limitations under the License.
 * 
 *     When distributing the software, include this License Header Notice in each
 *     file and include the License file at glassfish/legal/LICENSE.txt.
 * 
 *     GPL Classpath Exception:
 *     The Payara Foundation designates this particular file as subject to the "Classpath"
 *     exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 *     file that accompanied this code.
 * 
 *     Modifications:
 *     If applicable, add the following below the License Header, with the fields
 *     enclosed by brackets [] replaced by your own identifying information:
 *     "Portions Copyright [year] [name of copyright owner]"
 * 
 *     Contributor(s):
 *     If you wish your version of this file to be governed by only the CDDL or
 *     only the GPL Version 2, indicate your decision by adding "[Contributor]
 *     elects to include this software in this distribution under the [CDDL or GPL
 *     Version 2] license."  If you don't indicate a single choice of license, a
 *     recipient has the option to distribute your version of this file under
 *     either the CDDL, the GPL Version 2 or to extend the choice of license to
 *     its licensees as provided above.  However, if you add GPL Version 2 code
 *     and therefore, elected the GPL Version 2 license, then the option applies
 *     only if the new code is made subject to such option by the copyright
 *     holder.
 */
package fish.payara.nucleus.executorservice;

import java.util.concurrent.TimeUnit;
import org.glassfish.api.admin.config.ConfigExtension;
import org.jvnet.hk2.config.Attribute;
import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.config.Configured;

/**
 *
 * @author Andrew Pielage
 */
@Configured
public interface PayaraExecutorServiceConfiguration extends ConfigBeanProxy, ConfigExtension {
    
    @Attribute(defaultValue = "3", dataType = Integer.class)
    public String getThreadPoolExecutorCorePoolSize();
    public void setThreadPoolExecutorCorePoolSize(int corePoolSize);
    
    @Attribute(defaultValue = "20", dataType = Integer.class)
    public String getThreadPoolExecutorMaxPoolSize();
    public void setThreadPoolExecutorMaxPoolSize(int maxPoolSize);
    
    @Attribute(defaultValue = "1", dataType = Long.class)
    public String getThreadPoolExecutorKeepAliveTime();
    public void setThreadPoolExecutorKeepAliveTime(String keepAliveTime);
    
    @Attribute(defaultValue = "MINUTES")
    public String getThreadPoolExecutorKeepAliveTimeUnit();
    public void setThreadPoolExecutorKeepAliveTimeUnit(String keepAliveTimeUnit);
    
    @Attribute(defaultValue = "30", dataType = Integer.class)
    public String getThreadPoolExecutorQueueSize();
    public void setThreadPoolExecutorQueueSize(int queueSize);
    
    @Attribute(defaultValue = "3", dataType = Integer.class)
    public String getScheduledThreadPoolExecutorCorePoolSize();
    public void setScheduledThreadPoolExecutorCorePoolSize(int corePoolSize);
}
