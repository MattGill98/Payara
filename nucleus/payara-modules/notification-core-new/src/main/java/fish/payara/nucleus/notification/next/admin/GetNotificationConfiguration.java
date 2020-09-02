/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2016-2019 Payara Foundation and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://github.com/payara/Payara/blob/master/LICENSE.txt
 * See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * The Payara Foundation designates this particular file as subject to the "Classpath"
 * exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package fish.payara.nucleus.notification.next.admin;

import static fish.payara.nucleus.notification.next.admin.NotifierUtilities.getNotifierName;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import com.sun.enterprise.config.serverbeans.Config;
import com.sun.enterprise.util.ColumnFormatter;
import com.sun.enterprise.util.SystemPropertyConstants;

import org.glassfish.api.ActionReport;
import org.glassfish.api.I18n;
import org.glassfish.api.Param;
import org.glassfish.api.admin.AdminCommand;
import org.glassfish.api.admin.AdminCommandContext;
import org.glassfish.api.admin.CommandLock;
import org.glassfish.api.admin.ExecuteOn;
import org.glassfish.api.admin.RestEndpoint;
import org.glassfish.api.admin.RestEndpoints;
import org.glassfish.api.admin.RuntimeType;
import org.glassfish.config.support.CommandTarget;
import org.glassfish.config.support.TargetType;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.internal.api.Target;
import org.jvnet.hk2.annotations.Service;

import fish.payara.notification.PayaraNotifier;
import fish.payara.notification.PayaraNotifierConfiguration;
import fish.payara.nucleus.notification.next.configuration.NotificationServiceConfiguration;

/**
 * Admin command to list Notification Configuration
 *
 * @author Susan Rai
 */
@Service(name = "get-new-notification-configuration")
@PerLookup
@CommandLock(CommandLock.LockType.NONE)
@I18n("get.notification.configuration")
@ExecuteOn({RuntimeType.DAS})
@TargetType(value = {CommandTarget.DAS, CommandTarget.STANDALONE_INSTANCE, CommandTarget.CLUSTER, CommandTarget.CLUSTERED_INSTANCE, CommandTarget.CONFIG})
@RestEndpoints({
    @RestEndpoint(configBean = NotificationServiceConfiguration.class,
            opType = RestEndpoint.OpType.GET,
            path = "get-new-notification-configuration",
            description = "List Notification Configuration")
})
public class GetNotificationConfiguration implements AdminCommand {

    @Inject
    private Target targetUtil;

    @Inject
    ServiceLocator habitat;

    @Param(name = "target", optional = true, defaultValue = SystemPropertyConstants.DAS_SERVER_NAME)
    String target;

    @Override
    public void execute(AdminCommandContext context) {
        Config config = targetUtil.getConfig(target);
        if (config == null) {
            context.getActionReport().setMessage("No such config named: " + target);
            context.getActionReport().setActionExitCode(ActionReport.ExitCode.FAILURE);
            return;
        }

        ActionReport mainActionReport = context.getActionReport();
        final NotificationServiceConfiguration notificationServiceConfiguration = config.getExtensionByType(NotificationServiceConfiguration.class);
        NotificationServiceConfiguration configuration = config.getExtensionByType(NotificationServiceConfiguration.class);
        List<ServiceHandle<PayaraNotifier>> allServiceHandles = habitat.getAllServiceHandles(PayaraNotifier.class);

        String headers[] = {"Enabled", "Notifier Enabled", "Notifier Noisy"};
        ColumnFormatter columnFormatter = new ColumnFormatter(headers);

        if (configuration.getNotifierConfigurationList().isEmpty()) {
            mainActionReport.setMessage("No notifiers defined");
        }
        else {
            Properties extraProps = new Properties();

            for (ServiceHandle<PayaraNotifier> serviceHandle : allServiceHandles) {
                final String name = getNotifierName(serviceHandle);
                Class<?> serviceConfigClass = serviceHandle.getService().getConfigurationClass();
                PayaraNotifierConfiguration notifierConfig = null;
                if (serviceConfigClass != null) {
                    notifierConfig = configuration.getNotifierConfigurationByType(serviceConfigClass);
                }

                Map<String, Object> values = new LinkedHashMap<>();
                values.put("enabled", notificationServiceConfiguration.getEnabled());
                values.put("notifierEnabled", notifierConfig.getEnabled());
                values.put("noisy", notifierConfig.getNoisy());
                
                columnFormatter.addRow(values.values().toArray());
                extraProps.put(name, values);
            }
            mainActionReport.setExtraProperties(extraProps);
            mainActionReport.setMessage(columnFormatter.toString());
        }

        mainActionReport.setActionExitCode(ActionReport.ExitCode.SUCCESS);
    }

}
