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

import java.util.Hashtable;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.everit.osgi.authentication.simple.core.ActivationService;
import org.everit.osgi.authentication.simple.core.CredentialEncryptor;
import org.everit.osgi.authentication.simple.core.CredentialService;
import org.everit.osgi.authentication.simple.core.SimpleAuthenticationConstants;
import org.everit.osgi.authentication.simple.core.SimpleSubjectService;
import org.everit.osgi.resource.api.ResourceService;
import org.everit.osgi.transaction.helper.api.TransactionHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;

import com.mysema.query.sql.SQLTemplates;

@Component(name = SimpleAuthenticationConstants.COMPONENT_NAME, metatype = true, configurationFactory = true,
        policy = ConfigurationPolicy.REQUIRE)
@Properties({
        @Property(name = SimpleAuthenticationConstants.PROP_TRANSACTION_HELPER),
        @Property(name = SimpleAuthenticationConstants.PROP_DATA_SOURCE),
        @Property(name = SimpleAuthenticationConstants.PROP_SQL_TEMPLATES),
        @Property(name = SimpleAuthenticationConstants.PROP_RESOURCE_SERVICE),
        @Property(name = SimpleAuthenticationConstants.PROP_CREDENTIAL_ENCRYPTOR)
})
public class SimpleAuthenticationComponent {

    @Reference
    private TransactionHelper transactionHelper;

    @Reference
    private DataSource dataSource;

    @Reference
    private SQLTemplates sqlTemplates;

    @Reference
    private ResourceService resourceService;

    @Reference
    private CredentialEncryptor credentialEncryptor;

    private ServiceRegistration<?> simpleAuthenticationSR;

    @Activate
    public void activate(final BundleContext context, final Map<String, Object> componentProperties)
            throws ConfigurationException {
        Hashtable<String, Object> serviceProperties = new Hashtable<>(); // TODO define service properties
        SimpleAuthenticationImpl simpleAuthenticationImpl = new SimpleAuthenticationImpl(transactionHelper, dataSource,
                sqlTemplates, resourceService, credentialEncryptor);
        simpleAuthenticationSR =
                context.registerService(
                        new String[] {
                                SimpleSubjectService.class.getName(),
                                ActivationService.class.getName(),
                                CredentialService.class.getName() },
                        simpleAuthenticationImpl,
                        serviceProperties);
    }

    public void bindCredentialEncryptor(final CredentialEncryptor credentialEncryptor) {
        this.credentialEncryptor = credentialEncryptor;
    }

    public void bindDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void bindResourceService(final ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public void bindSqlTemplates(final SQLTemplates sqlTemplates) {
        this.sqlTemplates = sqlTemplates;
    }

    public void bindTransactionHelper(final TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    @Deactivate
    public void deactivate() {
        if (simpleAuthenticationSR != null) {
            simpleAuthenticationSR.unregister();
            simpleAuthenticationSR = null;
        }
    }

}
