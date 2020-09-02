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
package fish.payara.nucleus.notification.admin;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
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

import fish.payara.notification.PayaraConfiguredNotifier;
import fish.payara.notification.PayaraNotifier;
import fish.payara.notification.PayaraNotifierConfiguration;
import fish.payara.notification.admin.NotificationServiceConfiguration;

/**
 * Admin command to list Notification Configuration
 *
 * @author Susan Rai
 */
@Service(name = "get-notification-configuration")
@PerLookup
@CommandLock(CommandLock.LockType.NONE)
@I18n("get.notification.configuration")
@ExecuteOn({RuntimeType.DAS})
@TargetType(value = CommandTarget.CONFIG)
@RestEndpoints({
    @RestEndpoint(configBean = NotificationServiceConfiguration.class,
            opType = RestEndpoint.OpType.GET,
            path = "get-notification-configuration",
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

        // Get the command report
        final ActionReport report = context.getActionReport();

        // Get the target configuration
        final Config targetConfig = targetUtil.getConfig(target);
        if (targetConfig == null) {
            report.setMessage("No such config named: " + target);
            report.setActionExitCode(ActionReport.ExitCode.FAILURE);
            return;
        }

        final NotificationServiceConfiguration configuration = targetConfig.getExtensionByType(NotificationServiceConfiguration.class);
        final String notificationServiceEnabled = configuration.getEnabled();
        final List<PayaraNotifierConfiguration> notifierConfigurations = configuration.getNotifierConfigurationList();

        if (notifierConfigurations.isEmpty()) {
            report.setMessage("No notifiers defined");
            report.setActionExitCode(ActionReport.ExitCode.WARNING);
            return;
        }

        String headers[] = {"Enabled", "Notifier Enabled", "Notifier Noisy"};
        ColumnFormatter columnFormatter = new ColumnFormatter(headers);

        Properties extraProps = new Properties();
        for (ServiceHandle<PayaraNotifier> serviceHandle : habitat.getAllServiceHandles(PayaraNotifier.class)) {

            Object values[] = new Object[3];
            if (serviceHandle.getService() instanceof PayaraConfiguredNotifier) {
                // Get the associated configuration
                ParameterizedType genericSuperclass = (ParameterizedType) serviceHandle.getService().getClass().getGenericSuperclass();
                Class<PayaraNotifierConfiguration> notifierConfigurationClass = (Class<PayaraNotifierConfiguration>) genericSuperclass.getActualTypeArguments()[0];
                PayaraNotifierConfiguration notifierConfiguration = configuration.getNotifierConfigurationByType(notifierConfigurationClass);

                values[0] = notificationServiceEnabled;
                values[1] = notifierConfiguration.getEnabled();
                values[2] = notifierConfiguration.getNoisy();
            } else {
                values[0] = notificationServiceEnabled;
                values[1] = notificationServiceEnabled;
                values[2] = "N/A";
            }
            columnFormatter.addRow(values);

            Map<String, Object> map = new HashMap<>(3);
            map.put("enabled", values[0]);
            map.put("notifierEnabled", values[1]);
            map.put("noisy", values[2]);
            extraProps.put("getNotificationConfiguration." + serviceHandle.getActiveDescriptor().getClassAnalysisName(), map);
        }

        report.setMessage(columnFormatter.toString());
        report.setActionExitCode(ActionReport.ExitCode.SUCCESS);
        report.setExtraProperties(extraProps);
    }

}
