/**
 * This file is part of org.everit.osgi.authentication.simple.tests.core.
 *
 * org.everit.osgi.authentication.simple.tests.core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * org.everit.osgi.authentication.simple.tests.core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.everit.osgi.authentication.simple.tests.core.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.authentication.simple.tests.core;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

@Component(immediate = true)
@Properties({})
@Service(value = ConfigurationInitComponent.class)
public class ConfigurationInitComponent {

    @Reference
    private ConfigurationAdmin configAdmin;

    @Activate
    public void activate(final BundleContext bundleContext) throws Exception {
        getOrCreateConfiguration(CredentialEncryptorTestConstants.COMPONENT_NAME, new Hashtable<String, String>());
        getOrCreateConfiguration(SimpleAuthenticationTestConstants.COMPONENT_NAME, new Hashtable<String, String>());
    }

    public void bindConfigAdmin(final ConfigurationAdmin configAdmin) {
        this.configAdmin = configAdmin;
    }

    @Deactivate
    public void deactivate() throws Exception {
    }

    private String getOrCreateConfiguration(final String factoryPid, final Dictionary<String, String> props)
            throws IOException, InvalidSyntaxException {
        Configuration[] configurations = configAdmin.listConfigurations("(service.factoryPid=" + factoryPid + ")");
        if ((configurations != null) && (configurations.length > 0)) {
            return configurations[0].getPid();
        }
        Configuration configuration = configAdmin.createFactoryConfiguration(factoryPid, null);
        configuration.update(props);
        return configuration.getPid();
    }

}
