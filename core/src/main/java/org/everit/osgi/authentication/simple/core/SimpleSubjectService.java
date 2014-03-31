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
package org.everit.osgi.authentication.simple.core;

public interface SimpleSubjectService {

    /**
     * Creates a simple subject linked to the given resourceId or to the newly create resource if no resourceId
     * provided.
     * 
     * @param resourceId
     *            The ID of the resource to assign to the authenticated resource, a new resource will be created and
     *            assigned if <code>null</code> is provided.
     * @param principal
     *            The unique principal of the simple subject. Cannot be <code>null</code>.
     * @param plainCredential
     *            The secret plain credential (password, private key, etc.).
     * @param active
     *            The status of the simple subject, if active than the subject can log in, otherwise not.
     * @return The simple subject.
     */
    SimpleSubject create(Long resourceId, String principal, String plainCredential, boolean active);

    /**
     * Deletes the simple subject by principal.
     * 
     * @param principal
     *            The principal of the simple subject.
     */
    long delete(String principal);

    /**
     * Returns the {@link SimpleSubject} by principal.
     * 
     * @param principal
     *            The principal of the {@link SimpleSubject}.
     * @return The {@link SimpleSubject} or <code>null</code> if no {@link SimpleSubject} found.
     */
    SimpleSubject readByPrincipal(String principal);

    /**
     * Updates the principal of an authenticated resource.
     * 
     * @param principal
     *            The principal of the {@link SimpleSubject}. Cannot be <code>null</code>.
     * @param newPrincipal
     *            The new principal of the {@link SimpleSubject}. Cannot be <code>null</code>.
     * @return The updated {@link SimpleSubject}.
     */
    long updatePrincipal(String principal, String newPrincipal);

}
