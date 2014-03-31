/**
 * This file is part of org.everit.osgi.authentication.simple.core.
 *
 * org.everit.osgi.authentication.simple.core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * org.everit.osgi.authentication.simple.core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.everit.osgi.authentication.simple.core.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.authentication.simple.core.internal;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.everit.osgi.authentication.simple.core.CredentialEncryptor;
import org.everit.osgi.authentication.simple.core.CredentialEncryptorConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;

@Component(name = CredentialEncryptorConstants.COMPONENT_NAME, metatype = true, configurationFactory = true,
        policy = ConfigurationPolicy.REQUIRE)
@Properties({
        @Property(name = CredentialEncryptorConstants.PROP_ALGORITHM,
                value = CredentialEncryptorConstants.DEF_ALGORITHM)
})
public class CredentialEncryptorComponent {

    private ServiceRegistration<CredentialEncryptor> credentialEncryptorSR;

    @Activate
    public void activate(final BundleContext context, final Map<String, Object> componentProperties)
            throws ConfigurationException {
        String algorithm = getStringProperty(componentProperties, CredentialEncryptorConstants.PROP_ALGORITHM);
        Hashtable<String, Object> serviceProperties = new Hashtable<>();
        serviceProperties.put(CredentialEncryptorConstants.PROP_ALGORITHM, algorithm);
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(algorithm); // TODO wire message digest like keystore component
        } catch (NoSuchAlgorithmException e) {
            throw new ConfigurationException(
                    CredentialEncryptorConstants.PROP_ALGORITHM, "algorithm [" + algorithm + "] is not available", e);
        }
        CredentialEncryptor credentialEncryptor = new CredentialEncryptorImpl(messageDigest);
        credentialEncryptorSR =
                context.registerService(CredentialEncryptor.class, credentialEncryptor, serviceProperties);
    }

    @Deactivate
    public void deactivate() {
        if (credentialEncryptorSR != null) {
            credentialEncryptorSR.unregister();
            credentialEncryptorSR = null;
        }
    }

    private String getStringProperty(final Map<String, Object> componentProperties, final String propertyName)
            throws ConfigurationException {
        Object value = componentProperties.get(propertyName);
        if (value == null) {
            throw new ConfigurationException(propertyName, "property not defined");
        }
        return String.valueOf(value);
    }

}
