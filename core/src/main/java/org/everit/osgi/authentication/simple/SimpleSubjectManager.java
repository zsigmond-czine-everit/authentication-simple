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
 * The service interface for {@link SimpleSubject} management.
 */
public interface SimpleSubjectManager {

    /**
     * Creates a {@link SimpleSubject} linked to the given resourceId.
     *
     * @param resourceId
     *            The ID of the resource to assign to the {@link SimpleSubject}.
     * @param principal
     *            The unique principal of the {@link SimpleSubject}.
     * @param plainCredential
     *            The secret plain credential (password, private key, etc.). <code>null</code> if the credential is not
     *            available at this point. A {@link SimpleSubject} with <code>null</code> credential won't be
     *            authenticated.
     * @return The simple subject.
     */
    SimpleSubject create(long resourceId, String principal, String plainCredential);

    /**
     * Deletes the simple subject by principal.
     *
     * @param principal
     *            The principal of the simple subject.
     * @return <code>true</code> if the simple subject was deleted successfully, otherwise <code>false</code>.
     */
    boolean delete(String principal);

    /**
     * Returns the encrypted credential of the {@link SimpleSubject} assigned to the given principal. This method is
     * generally used by credential matchers.
     *
     * @param principal
     *            The principal of the {@link SimpleSubject}.
     * @return The encrypted principal or <code>null</code> if no {@link SimpleSubject} exists with the given principal
     *         or the encrypted credential of the {@link SimpleSubject} is <code>null</code>.
     */
    String readEncryptedCredential(String principal);

    /**
     * Returns the {@link SimpleSubject} assigned to the given principal.
     *
     * @param principal
     *            The principal of the {@link SimpleSubject}.
     * @return The {@link SimpleSubject} or <code>null</code> if no {@link SimpleSubject} found.
     */
    SimpleSubject readSimpleSubjectByPrincipal(String principal);

    /**
     * Updates the credential of a {@link SimpleSubject}.
     *
     * @param principal
     *            The principal of the {@link SimpleSubject}.
     * @param newPlainCredential
     *            The new plain credential of the {@link SimpleSubject}. A {@link SimpleSubject} with <code>null</code>
     *            credential won't be authenticated.
     * @return <code>true</code> if the update was successful, otherwise <code>false</code>.
     */
    boolean updateCredential(String principal, String newPlainCredential);

    /**
     * Updates the credential of a {@link SimpleSubject} if the original plain credential matches the actual credential
     * of the {@link SimpleSubject}.
     *
     * @param principal
     *            The principal of the {@link SimpleSubject}.
     * @param originalPlainCredential
     *            The original plain credential of the {@link SimpleSubject} used to match the actual credential.
     * @param newPlainCredential
     *            The new plain credential of the {@link SimpleSubject}.
     * @return <code>true</code> if the update was successful, otherwise <code>false</code>.
     */
    boolean updateCredential(String principal, String originalPlainCredential, String newPlainCredential);

    /**
     * Updates the principal of a {@link SimpleSubject}.
     *
     * @param principal
     *            The principal of the {@link SimpleSubject}.
     * @param newPrincipal
     *            The new unique principal of the {@link SimpleSubject}.
     * @return <code>true</code> if the update was successful, otherwise <code>false</code>.
     */
    boolean updatePrincipal(String principal, String newPrincipal);

}
