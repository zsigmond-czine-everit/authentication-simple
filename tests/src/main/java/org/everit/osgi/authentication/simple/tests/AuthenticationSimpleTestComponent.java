/**
 * This file is part of Everit - Authentication Simple Tests.
 *
 * Everit - Authentication Simple Tests is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Everit - Authentication Simple Tests is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Everit - Authentication Simple Tests.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.authentication.simple.tests;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.everit.osgi.authentication.simple.SimpleSubject;
import org.everit.osgi.authentication.simple.SimpleSubjectManager;
import org.everit.osgi.authentication.simple.schema.qdsl.QSimpleSubject;
import org.everit.osgi.authenticator.Authenticator;
import org.everit.osgi.dev.testrunner.TestRunnerConstants;
import org.everit.osgi.querydsl.support.QuerydslSupport;
import org.everit.osgi.resource.ResourceService;
import org.everit.osgi.resource.resolver.ResourceIdResolver;
import org.everit.osgi.transaction.helper.api.TransactionHelper;
import org.junit.Assert;
import org.junit.Test;

import com.mysema.query.sql.dml.SQLDeleteClause;

@Component(name = "SimpleSubjectManagerTest", immediate = true, configurationFactory = false,
        policy = ConfigurationPolicy.OPTIONAL)
@Properties({
        @Property(name = TestRunnerConstants.SERVICE_PROPERTY_TESTRUNNER_ENGINE_TYPE, value = "junit4"),
        @Property(name = TestRunnerConstants.SERVICE_PROPERTY_TEST_ID, value = "SimpleSubjectManagerTest"),
        @Property(name = "simpleSubjectManager.target"),
        @Property(name = "authenticator.target"),
        @Property(name = "resourceIdResolver.target"),
        @Property(name = "resourceService.target"),
        @Property(name = "transactionHelper.target"),
        @Property(name = "querydslSupport.target")
})
@Service(value = AuthenticationSimpleTestComponent.class)
public class AuthenticationSimpleTestComponent {

    @Reference(bind = "setAuthenticator")
    private Authenticator authenticator;

    @Reference(bind = "setResourceIdResolver")
    private ResourceIdResolver resourceIdResolver;

    @Reference(bind = "setSimpleSubjectManager")
    private SimpleSubjectManager simpleSubjectManager;

    @Reference(bind = "setResourceService")
    private ResourceService resourceService;

    @Reference(bind = "setTransactionHelper")
    private TransactionHelper transactionHelper;

    @Reference(bind = "setQuerydslSupport")
    private QuerydslSupport querydslSupport;

    private SimpleSubject createWithResource(final String principal, final String plainCredential) {
        return transactionHelper.required(() -> {
            long resourceId = resourceService.createResource();
            SimpleSubject simpleSubject = simpleSubjectManager.create(resourceId, principal, plainCredential);
            Assert.assertNotNull(simpleSubject);
            Assert.assertEquals(principal, simpleSubject.getPrincipal());
            Assert.assertEquals(resourceId, simpleSubject.getResourceId());
            return simpleSubject;
        });
    }

    private void deleteAllSimpleSubjects() {
        querydslSupport.execute((connection, configuration) -> {
            QSimpleSubject qSimpleSubject = QSimpleSubject.simpleSubject;
            new SQLDeleteClause(connection, configuration, qSimpleSubject).execute();
            return null;
        });
    }

    public void setAuthenticator(final Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public void setQuerydslSupport(final QuerydslSupport querydslSupport) {
        this.querydslSupport = querydslSupport;
    }

    public void setResourceIdResolver(final ResourceIdResolver resourceIdResolver) {
        this.resourceIdResolver = resourceIdResolver;
    }

    public void setResourceService(final ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public void setSimpleSubjectManager(final SimpleSubjectManager simpleSubjectManager) {
        this.simpleSubjectManager = simpleSubjectManager;
    }

    public void setTransactionHelper(final TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    @Test
    public void testAuthenticator() {
        deleteAllSimpleSubjects();
        String principal = "principal";
        String plainCredential = "credential";
        createWithResource(principal, plainCredential);

        Assert.assertEquals(principal, authenticator.authenticate(principal, plainCredential).get());
        Assert.assertFalse(authenticator.authenticate(principal, plainCredential + plainCredential).isPresent());
        Assert.assertFalse(authenticator.authenticate(principal, null).isPresent());

        String newPlainCredential = "credential_new";
        simpleSubjectManager.updateCredential(principal, newPlainCredential);

        Assert.assertEquals(principal, authenticator.authenticate(principal, newPlainCredential).get());
        Assert.assertFalse(authenticator.authenticate(principal, plainCredential).isPresent());

        Assert.assertFalse(authenticator.authenticate(principal + principal, newPlainCredential).isPresent());

    }

    @Test
    public void testManager() {
        deleteAllSimpleSubjects();
        String principal = "principal";
        String plainCredential = "credential";

        SimpleSubject originalSimpleSubject = createWithResource(principal, plainCredential);

        SimpleSubject simpleSubject = simpleSubjectManager.readSimpleSubjectByPrincipal(principal);
        Assert.assertEquals(originalSimpleSubject, simpleSubject);

        String encryptedCredential = simpleSubjectManager.readEncryptedCredential(principal);
        Assert.assertNotNull(encryptedCredential);

        String newPrincipal = "principal_new";
        Assert.assertTrue(simpleSubjectManager.updatePrincipal(principal, newPrincipal));
        Assert.assertFalse(simpleSubjectManager.updatePrincipal(principal, newPrincipal));
        Assert.assertNull(simpleSubjectManager.readSimpleSubjectByPrincipal(principal));
        Assert.assertEquals(encryptedCredential, simpleSubjectManager.readEncryptedCredential(newPrincipal));

        simpleSubject = simpleSubjectManager.readSimpleSubjectByPrincipal(newPrincipal);
        Assert.assertEquals(originalSimpleSubject.getSimpleSubjectId(), simpleSubject.getSimpleSubjectId());
        Assert.assertEquals(originalSimpleSubject.getResourceId(), simpleSubject.getResourceId());
        Assert.assertEquals(newPrincipal, simpleSubject.getPrincipal());

        String newPlainCredential = "credential_new";
        Assert.assertFalse(simpleSubjectManager.updateCredential(principal, plainCredential, newPlainCredential));
        Assert.assertTrue(simpleSubjectManager.updateCredential(newPrincipal, plainCredential, newPlainCredential));
        Assert.assertNotEquals(encryptedCredential, simpleSubjectManager.readEncryptedCredential(newPrincipal));
        Assert.assertFalse(simpleSubjectManager.updateCredential(newPrincipal, plainCredential, newPlainCredential));

        Assert.assertFalse(simpleSubjectManager.updateCredential(principal, newPlainCredential));
        Assert.assertTrue(simpleSubjectManager.updateCredential(newPrincipal, null));
        Assert.assertTrue(simpleSubjectManager.updateCredential(newPrincipal, null, null));
        Assert.assertTrue(simpleSubjectManager.updateCredential(newPrincipal, null, newPlainCredential));

        Assert.assertFalse(simpleSubjectManager.delete(principal));
        Assert.assertTrue(simpleSubjectManager.delete(newPrincipal));
        Assert.assertFalse(simpleSubjectManager.delete(newPrincipal));
    }

    @Test
    public void testResourceIdResolver() {
        deleteAllSimpleSubjects();
        String principal = "principal";
        String plainCredential = "credential";
        SimpleSubject simpleSubject = createWithResource(principal, plainCredential);

        Assert.assertEquals(simpleSubject.getResourceId(),
                resourceIdResolver.getResourceId(principal).get().longValue());
        Assert.assertFalse(resourceIdResolver.getResourceId(principal + principal).isPresent());
    }

}
