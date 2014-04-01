/**
 * This file is part of org.everit.osgi.authentication.simple.
 *
 * org.everit.osgi.authentication.simple is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * org.everit.osgi.authentication.simple is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.everit.osgi.authentication.simple.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.authentication.simple.internal;

public final class SimpleAuthenticationConstants {

    public static final String COMPONENT_NAME = "org.everit.osgi.authentication.simple.SimpleAuthentication";

    public static final String PROP_TRANSACTION_HELPER = "transactionHelper.target";

    public static final String PROP_DATA_SOURCE = "dataSource.target";

    public static final String PROP_SQL_TEMPLATES = "sqlTemplates.target";

    public static final String PROP_RESOURCE_SERVICE = "resourceService.target";

    public static final String PROP_CREDENTIAL_ENCRYPTOR = "credentialEncryptor.target";

    private SimpleAuthenticationConstants() {
    }

}
