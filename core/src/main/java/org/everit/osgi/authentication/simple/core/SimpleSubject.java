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

import java.io.Serializable;

import org.everit.osgi.authentication.api.Subject;

public class SimpleSubject implements Subject, Serializable {

    private static final long serialVersionUID = 6536249841440027308L;

    private final long simpleSubjectId;

    private final String principal;

    private final boolean active;

    private final long resourceId;

    public SimpleSubject(final long simpleSubjectId, final String principal, final boolean active,
            final long resourceId) {
        super();
        this.simpleSubjectId = simpleSubjectId;
        this.principal = principal;
        this.active = active;
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
        if (active != other.active) {
            return false;
        }
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

    @Override
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
        result = (prime * result) + (active ? 1231 : 1237);
        result = (prime * result) + ((principal == null) ? 0 : principal.hashCode());
        result = (prime * result) + (int) (resourceId ^ (resourceId >>> 32));
        result = (prime * result) + (int) (simpleSubjectId ^ (simpleSubjectId >>> 32));
        return result;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return "SimpleSubject [simpleSubjectId=" + simpleSubjectId + ", principal=" + principal + ", active=" + active
                + ", resourceId=" + resourceId + "]";
    }

}
