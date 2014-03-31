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
package org.everit.osgi.authentication.simple.tests;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.everit.osgi.authentication.simple.core.CredentialEncryptor;
import org.everit.osgi.dev.testrunner.TestDuringDevelopment;
import org.everit.osgi.dev.testrunner.TestRunnerConstants;
import org.junit.Assert;
import org.junit.Test;

@Component(name = CredentialEncryptorTestConstants.COMPONENT_NAME, metatype = true,
        policy = ConfigurationPolicy.REQUIRE)
@Properties({
        @Property(name = TestRunnerConstants.SERVICE_PROPERTY_TESTRUNNER_ENGINE_TYPE, value = "junit4"),
        @Property(name = TestRunnerConstants.SERVICE_PROPERTY_TEST_ID, value = CredentialEncryptorTestConstants.TEST_ID),
        @Property(name = CredentialEncryptorTestConstants.PROP_CREDENTIAL_ENCRYPTOR)
})
@Service(value = CredentialEncryptorTestComponent.class)
@TestDuringDevelopment
public class CredentialEncryptorTestComponent {

    @Reference
    private CredentialEncryptor credentialEncryptor;

    public void bindCredentialEncryptor(final CredentialEncryptor credentialEncryptor) {
        this.credentialEncryptor = credentialEncryptor;
    }

    @Test
    public void testArgumentValidations() {
        try {
            credentialEncryptor.encryptCredential(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("plainCredential cannot be null", e.getMessage());
        }
        try {
            credentialEncryptor.checkCredential(null, null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("plainCredential cannot be null", e.getMessage());
        }
        try {
            credentialEncryptor.checkCredential("", null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("encryptedCredential cannot be null", e.getMessage());
        }
    }

    @Test
    public void testCredentialEncryptionAndValidation() {
        String encryptedCredential = credentialEncryptor.encryptCredential("foo");
        Assert.assertNotNull(encryptedCredential);
        boolean credentialEquals = credentialEncryptor.checkCredential("foo", encryptedCredential);
        Assert.assertTrue(credentialEquals);
        credentialEquals = credentialEncryptor.checkCredential("bar", encryptedCredential);
        Assert.assertFalse(credentialEquals);
    }

    @Test
    public void testPlainCredentialHandling() {
        boolean credentialsEquals = credentialEncryptor.checkCredential("foo", "{plain}foo");
        Assert.assertTrue(credentialsEquals);
        credentialsEquals = credentialEncryptor.checkCredential("foo", "{plain}bar");
        Assert.assertFalse(credentialsEquals);
    }

}
