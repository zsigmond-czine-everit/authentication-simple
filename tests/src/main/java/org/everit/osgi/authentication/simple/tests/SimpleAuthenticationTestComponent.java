/**
 * This file is part of org.everit.osgi.authentication.simple.tests.
 *
 * org.everit.osgi.authentication.simple.tests is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * org.everit.osgi.authentication.simple.tests is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.everit.osgi.authentication.simple.tests.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.authentication.simple.tests;

import java.sql.SQLException;
import java.util.UUID;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.everit.osgi.authentication.simple.ActivationService;
import org.everit.osgi.authentication.simple.CredentialService;
import org.everit.osgi.authentication.simple.SimpleSubject;
import org.everit.osgi.authentication.simple.SimpleSubjectService;
import org.everit.osgi.authentication.simple.schema.Validation;
import org.everit.osgi.dev.testrunner.TestRunnerConstants;
import org.everit.osgi.resource.api.ResourceService;
import org.junit.Assert;
import org.junit.Test;

@Component(name = "SimpleAuthenticationTest", immediate = true, configurationFactory = false,
        policy = ConfigurationPolicy.OPTIONAL)
@Properties({
        @Property(name = TestRunnerConstants.SERVICE_PROPERTY_TESTRUNNER_ENGINE_TYPE, value = "junit4"),
        @Property(name = TestRunnerConstants.SERVICE_PROPERTY_TEST_ID, value = "SimpleAuthenticationTest"),
        @Property(name = "activationService.target"),
        @Property(name = "credentialService.target"),
        @Property(name = "simpleSubjectService.target"),
        @Property(name = "resourceService.target")
})
@Service(value = SimpleAuthenticationTestComponent.class)
public class SimpleAuthenticationTestComponent {

    private static final String LONG_STRING =
            "12345678901234567890123456789012345678901234567890"
                    + "12345678901234567890123456789012345678901234567890"
                    + "12345678901234567890123456789012345678901234567890"
                    + "12345678901234567890123456789012345678901234567890"
                    + "12345678901234567890123456789012345678901234567890"
                    + "12345678901234567890123456789012345678901234567890";

    @Reference
    private ActivationService activationService;

    @Reference
    private CredentialService credentialService;

    @Reference
    private SimpleSubjectService simpleSubjectService;

    @Reference
    private ResourceService resourceService;

    public void bindActivationService(final ActivationService activationService) {
        this.activationService = activationService;
    }

    public void bindCredentialService(final CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    public void bindResourceService(final ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public void bindSimpleSubjectService(final SimpleSubjectService simpleSubjectService) {
        this.simpleSubjectService = simpleSubjectService;
    }

    @Test
    public void testActivationAndDeactivation() {
        String principal = UUID.randomUUID().toString();
        simpleSubjectService.create(null, principal, principal, true);
        Assert.assertEquals(1, activationService.setActive(principal, false));
        Assert.assertFalse(activationService.isActive(principal));
        Assert.assertEquals(1, activationService.activateByPrincipal(principal));
        Assert.assertTrue(activationService.isActive(principal));
        Assert.assertEquals(1, activationService.deactivate(principal));
        Assert.assertFalse(activationService.isActive(principal));
        Assert.assertEquals(1, activationService.setActive(principal, true));
        Assert.assertTrue(activationService.isActive(principal));

        String nonExistentPrincipal = UUID.randomUUID().toString();
        Assert.assertEquals(0, activationService.setActive(nonExistentPrincipal, false));
        Assert.assertEquals(0, activationService.activateByPrincipal(nonExistentPrincipal));
        Assert.assertEquals(0, activationService.deactivate(nonExistentPrincipal));
        Assert.assertFalse(activationService.isActive(nonExistentPrincipal));
        Assert.assertEquals(0, activationService.setActive(nonExistentPrincipal, true));
    }

    @Test
    public void testArgumentValidations() {
        try {
            activationService.activateByPrincipal(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("principal cannot be null", e.getMessage());
        }
        try {
            activationService.deactivate(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("principal cannot be null", e.getMessage());
        }
        try {
            activationService.isActive(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("principal cannot be null", e.getMessage());
        }
        try {
            activationService.setActive(null, true);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("principal cannot be null", e.getMessage());
        }

        try {
            credentialService.getCredential(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("principal cannot be null", e.getMessage());
        }

        try {
            simpleSubjectService.create(null, null, null, true);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("principal cannot be null", e.getMessage());
        }
        try {
            simpleSubjectService.create(null, "", null, true);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("principal cannot be empty", e.getMessage());
        }
        try {
            simpleSubjectService.create(null, LONG_STRING, null, true);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("principal cannot be longer than " + Validation.PRINCIPAL_MAX_LENGTH, e.getMessage());
        }
        try {
            simpleSubjectService.create(null, "foo", null, true);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("plainCredential cannot be null", e.getMessage());
        }
        try {
            simpleSubjectService.create(null, "foo", "", true);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("plainCredential cannot be empty", e.getMessage());
        }
        try {
            simpleSubjectService.create(null, "foo", LONG_STRING, true);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("plainCredential cannot be longer than " + Validation.CREDENTIAL_MAX_LENGTH,
                    e.getMessage());
        }
        try {
            simpleSubjectService.delete(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("principal cannot be null", e.getMessage());
        }
        try {
            simpleSubjectService.readByPrincipal(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("principal cannot be null", e.getMessage());
        }
        try {
            simpleSubjectService.updatePrincipal(null, null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("principal cannot be null", e.getMessage());
        }
        try {
            simpleSubjectService.updatePrincipal("foo", null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("newPrincipal cannot be null", e.getMessage());
        }
        try {
            simpleSubjectService.updatePrincipal("foo", "");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("newPrincipal cannot be empty", e.getMessage());
        }
        try {
            simpleSubjectService.updatePrincipal("foo", LONG_STRING);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("newPrincipal cannot be longer than " + Validation.PRINCIPAL_MAX_LENGTH,
                    e.getMessage());
        }
    }

    @Test
    public void testCredentialService() {
        String principal = UUID.randomUUID().toString();
        simpleSubjectService.create(null, principal, principal, true);
        String credential = credentialService.getCredential(principal);
        Assert.assertNotNull(credential);
        credential = credentialService.getCredential(credential);
        Assert.assertNull(credential);
        Assert.assertNotEquals(principal, credential);
    }

    @Test
    public void testSimpleSubjectServiceCreate() {
        String principal1 = UUID.randomUUID().toString();
        SimpleSubject simpleSubject = simpleSubjectService.create(null, principal1, principal1, true);
        Assert.assertNotNull(simpleSubject);
        Assert.assertEquals(principal1, simpleSubject.getPrincipal());
        Assert.assertTrue(simpleSubject.getResourceId() != 0);
        Assert.assertTrue(simpleSubject.getSimpleSubjectId() != 0);
        Assert.assertTrue(simpleSubject.isActive());

        long resourceId = resourceService.createResource();
        String principal2 = UUID.randomUUID().toString();
        simpleSubject = simpleSubjectService.create(resourceId, principal2, principal2, false);
        Assert.assertNotNull(simpleSubject);
        Assert.assertEquals(principal2, simpleSubject.getPrincipal());
        Assert.assertEquals(resourceId, simpleSubject.getResourceId());
        Assert.assertTrue(simpleSubject.getSimpleSubjectId() != 0);
        Assert.assertFalse(simpleSubject.isActive());

        try {
            simpleSubjectService.create(resourceId, principal1, principal2, true);
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }
    }

    @Test
    public void testSimpleSubjectServiceDelete() {
        String principal = UUID.randomUUID().toString();
        long deletedRecords = simpleSubjectService.delete(principal);
        Assert.assertEquals(0, deletedRecords);
        simpleSubjectService.create(null, principal, principal, true);
        deletedRecords = simpleSubjectService.delete(principal);
        Assert.assertEquals(1, deletedRecords);
        deletedRecords = simpleSubjectService.delete(principal);
        Assert.assertEquals(0, deletedRecords);
    }

    @Test
    public void testSimpleSubjectServiceRead() {
        String principal = UUID.randomUUID().toString();
        SimpleSubject simpleSubject = simpleSubjectService.readByPrincipal(principal);
        Assert.assertNull(simpleSubject);
        simpleSubjectService.create(null, principal, principal, true);
        simpleSubject = simpleSubjectService.readByPrincipal(principal);
        Assert.assertNotNull(simpleSubject);
        Assert.assertEquals(principal, simpleSubject.getPrincipal());
        Assert.assertTrue(simpleSubject.getResourceId() != 0);
        Assert.assertTrue(simpleSubject.getSimpleSubjectId() != 0);
        Assert.assertTrue(simpleSubject.isActive());
    }

    @Test
    public void testSimpleSubjectServiceUpdatePrincipal() {
        String principal = UUID.randomUUID().toString();
        String newPrincipal = UUID.randomUUID().toString();
        long updatedRecords = simpleSubjectService.updatePrincipal(principal, newPrincipal);
        Assert.assertEquals(0, updatedRecords);
        simpleSubjectService.create(null, principal, newPrincipal, true);
        updatedRecords = simpleSubjectService.updatePrincipal(principal, newPrincipal);
        Assert.assertEquals(1, updatedRecords);
        SimpleSubject simpleSubject = simpleSubjectService.readByPrincipal(principal);
        Assert.assertNull(simpleSubject);
        simpleSubject = simpleSubjectService.readByPrincipal(newPrincipal);
        Assert.assertNotNull(simpleSubject);
    }

}
