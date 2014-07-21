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
 * A principal and credential based subject assigned to a resource. This can be a base of a user in a system.
 */
public class SimpleSubject {

    /**
     * The ID of the simple subject.
     */
    private final long simpleSubjectId;

    /**
     * The unique principal of the simple subject. For e.g. user name, email address.
     */
    private final String principal;

    /**
     * The resource ID assigned to the simple subject.
     */
    private final long resourceId;

    /**
     * Constructor.
     * 
     * @param simpleSubjectId
     * @param principal
     * @param resourceId
     */
    public SimpleSubject(final long simpleSubjectId, final String principal, final long resourceId) {
        super();
        this.simpleSubjectId = simpleSubjectId;
        this.principal = principal;
        this.resourceId = resourceId;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SimpleSubject other = (SimpleSubject) obj;
        if (principal == null) {
            if (other.principal != null) {
                return false;
            }
        } else if (!principal.equals(other.principal)) {
            return false;
        }
        if (resourceId != other.resourceId) {
            return false;
        }
        if (simpleSubjectId != other.simpleSubjectId) {
            return false;
        }
        return true;
    }

    public String getPrincipal() {
        return principal;
    }

    public long getResourceId() {
        return resourceId;
    }

    public long getSimpleSubjectId() {
        return simpleSubjectId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((principal == null) ? 0 : principal.hashCode());
        result = (prime * result) + (int) (resourceId ^ (resourceId >>> 32));
        result = (prime * result) + (int) (simpleSubjectId ^ (simpleSubjectId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "SimpleSubject [simpleSubjectId=" + simpleSubjectId + ", principal=" + principal + ", resourceId="
                + resourceId + "]";
    }

}
