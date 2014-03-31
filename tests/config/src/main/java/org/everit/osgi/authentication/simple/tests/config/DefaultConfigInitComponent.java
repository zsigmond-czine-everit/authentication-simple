/**
 * This file is part of org.everit.osgi.authentication.simple.tests.config.
 *
 * org.everit.osgi.authentication.simple.tests.config is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * org.everit.osgi.authentication.simple.tests.config is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.everit.osgi.authentication.simple.tests.config.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.authentication.simple.tests.config;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.everit.osgi.authentication.simple.core.CredentialEncryptorConstants;
import org.everit.osgi.authentication.simple.core.SimpleAuthenticationConstants;
import org.everit.osgi.querydsl.templates.SQLTemplatesConstants;
import org.everit.osgi.resource.api.ResourceConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.jdbc.DataSourceFactory;

@Component(immediate = true)
@Properties({})
@Service(value = DefaultConfigInitComponent.class)
public class DefaultConfigInitComponent {

    @Reference
    private ConfigurationAdmin configAdmin;

    @Activate
    public void activate(final BundleContext bundleContext) throws Exception {
        Hashtable<String, Object> credentialEncryptorProperties = new Hashtable<String, Object>();
        credentialEncryptorProperties.put(CredentialEncryptorConstants.PROP_ALGORITHM,
                CredentialEncryptorConstants.DEF_ALGORITHM);
        getOrCreateConfiguration(CredentialEncryptorConstants.COMPONENT_NAME, credentialEncryptorProperties);

        String schemaExpression = "org.everit.osgi.authentication.simple";
        createDefaultRequirements(schemaExpression);

        Hashtable<String, Object> simpleAuthenticationProperties = new Hashtable<String, Object>();
        // FIXME replace hard coded string with constant
        simpleAuthenticationProperties.put(SimpleAuthenticationConstants.PROP_DATA_SOURCE,
                "(liquibase.schema.expression=" + schemaExpression + ")");
        getOrCreateConfiguration(SimpleAuthenticationConstants.COMPONENT_NAME, simpleAuthenticationProperties);
    }

    public void bindConfigAdmin(final ConfigurationAdmin configAdmin) {
        this.configAdmin = configAdmin;
    }

    // FIXME remove these component creation, these should be done by the *.tests.config.DefaultConfigInitComponent
    private void createDefaultRequirements(final String schemaExpression) throws IOException, InvalidSyntaxException {
        Dictionary<String, Object> xaDataSourceProps = new Hashtable<String, Object>();
        xaDataSourceProps.put(DataSourceFactory.JDBC_URL, "jdbc:h2:mem:test");
        String xaDataSourcePid = getOrCreateConfiguration("org.everit.osgi.jdbc.dsf.XADataSourceComponent",
                xaDataSourceProps);

        Dictionary<String, Object> pooledDataSourceProps = new Hashtable<String, Object>();
        pooledDataSourceProps.put("xaDataSource.target", "(service.pid=" + xaDataSourcePid + ")");
        String pooledDataSourcePid = getOrCreateConfiguration(
                "org.everit.osgi.jdbc.commons.dbcp.ManagedDataSourceComponent",
                pooledDataSourceProps);

        Dictionary<String, Object> sqlTemplatesProps = new Hashtable<String, Object>();
        sqlTemplatesProps.put("dataSource.target", "(service.pid=" + pooledDataSourcePid + ")");
        sqlTemplatesProps.put(SQLTemplatesConstants.PROP_QUOTE, true);
        getOrCreateConfiguration(SQLTemplatesConstants.COMPONENT_NAME_AUTO_SQL_TEMPLATES, sqlTemplatesProps);

        Dictionary<String, Object> migratedDataSourceProps = new Hashtable<String, Object>();
        migratedDataSourceProps.put("embeddedDataSource.target", "(service.pid=" + pooledDataSourcePid + ")");
        migratedDataSourceProps.put("schemaExpression", schemaExpression);
        String liquiBaseDataSorucePid = getOrCreateConfiguration(
                "org.everit.osgi.liquibase.datasource.LiquibaseDataSourceComponent", migratedDataSourceProps);

        Dictionary<String, Object> resourceProps = new Hashtable<String, Object>();
        resourceProps.put(ResourceConstants.PROP_DATASOURCE_TARGET, "(" + Constants.SERVICE_PID + "="
                + liquiBaseDataSorucePid + ")");
        getOrCreateConfiguration(ResourceConstants.COMPONENT_NAME_RESOURCE, resourceProps);
    }

    @Deactivate
    public void deactivate() throws Exception {
    }

    private String getOrCreateConfiguration(final String factoryPid, final Dictionary<String, Object> props)
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
