/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.biz)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.authentication.simple;

/**
 * A principal and credential based subject assigned to a resource. This can be a base of a user in
 * a system.
 */
public class SimpleSubject {

  /**
   * The unique principal of the simple subject. For e.g. user name, email address.
   */
  private final String principal;

  /**
   * The resource ID assigned to the simple subject.
   */
  private final long resourceId;

  /**
   * The ID of the simple subject.
   */
  private final long simpleSubjectId;

  /**
   * Constructor.
   *
   * @param simpleSubjectId
   *          The ID of the simple subject.
   * @param principal
   *          The unique principal of the simple subject. For e.g. user name, email address.
   * @param resourceId
   *          The resource ID assigned to the simple subject.
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
    return "SimpleSubject [simpleSubjectId=" + simpleSubjectId + ", principal=" + principal
        + ", resourceId="
        + resourceId + "]";
  }

}
