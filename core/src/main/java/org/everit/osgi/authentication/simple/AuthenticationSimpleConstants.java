/**
 * This file is part of Everit - Authentication Simple.
 *
 * Everit - Authentication Simple is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Everit - Authentication Simple is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Everit - Authentication Simple.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.authentication.simple;

/**
 * Constants of the Authentication Simple component.
 */
public final class AuthenticationSimpleConstants {

    /**
     * The service factory PID of the Authentication Simple component.
     */
    public static final String SERVICE_FACTORYPID_AUTHENTICATION_SIMPLE =
            "org.everit.osgi.authentication.simple.AuthenticationSimple";

    /**
     * The property name of the OSGi filter expression defining which QuerydslSupport should be used.
     */
    public static final String PROP_QUERYDSL_SUPPORT = "querydslSupport.target";

    /**
     * The property name of the OSGi filter expression defining which CredentialEncryptor should be used.
     */
    public static final String PROP_CREDENTIAL_ENCRYPTOR = "credentialEncryptor.target";

    /**
     * The property name of the OSGi filter expression defining which CredentialMatcher should be used.
     */
    public static final String PROP_CREDENTIAL_MATCHER = "credentialMatcher.target";

    /**
     * The property name of the OSGi filter expression defining which LogService should be used.
     */
    public static final String PROP_LOG_SERVICE = "logService.target";

    private AuthenticationSimpleConstants() {
    }

}
