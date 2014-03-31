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

public interface ActivationService {

    long activateByPrincipal(String principal);

    long deactivate(String principal);

    boolean isActive(String principal);

    long setActive(String principal, boolean active);

}
