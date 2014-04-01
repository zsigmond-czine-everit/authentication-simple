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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.everit.osgi.authentication.simple.ActivationService;
import org.everit.osgi.authentication.simple.CredentialEncryptor;
import org.everit.osgi.authentication.simple.CredentialService;
import org.everit.osgi.authentication.simple.SimpleSubject;
import org.everit.osgi.authentication.simple.SimpleSubjectService;
import org.everit.osgi.authentication.simple.schema.Validation;
import org.everit.osgi.authentication.simple.schema.qdsl.QSimpleSubject;
import org.everit.osgi.resource.api.ResourceService;
import org.everit.osgi.transaction.helper.api.Callback;
import org.everit.osgi.transaction.helper.api.TransactionHelper;

import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.sql.dml.SQLDeleteClause;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.types.ConstructorExpression;

@Component(name = SimpleAuthenticationConstants.COMPONENT_NAME, metatype = true, configurationFactory = true,
        policy = ConfigurationPolicy.REQUIRE)
@Properties({
        @Property(name = SimpleAuthenticationConstants.PROP_TRANSACTION_HELPER),
        @Property(name = SimpleAuthenticationConstants.PROP_DATA_SOURCE),
        @Property(name = SimpleAuthenticationConstants.PROP_SQL_TEMPLATES),
        @Property(name = SimpleAuthenticationConstants.PROP_RESOURCE_SERVICE),
        @Property(name = SimpleAuthenticationConstants.PROP_CREDENTIAL_ENCRYPTOR)
})
@Service
public class SimpleAuthenticationComponent implements SimpleSubjectService, ActivationService, CredentialService {

    @Reference
    private TransactionHelper transactionHelper;

    @Reference
    private DataSource dataSource;

    @Reference
    private SQLTemplates sqlTemplates;

    @Reference
    private ResourceService resourceService;

    @Reference
    private CredentialEncryptor credentialEncryptor;

    @Override
    public long activateByPrincipal(final String principal) {
        return setActive(principal, true);
    }

    public void bindCredentialEncryptor(final CredentialEncryptor credentialEncryptor) {
        this.credentialEncryptor = credentialEncryptor;
    }

    public void bindDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void bindResourceService(final ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public void bindSqlTemplates(final SQLTemplates sqlTemplates) {
        this.sqlTemplates = sqlTemplates;
    }

    public void bindTransactionHelper(final TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    @Override
    public SimpleSubject create(final Long resourceId, final String principal, final String plainCredential,
            final boolean active) {
        if (principal == null) {
            throw new IllegalArgumentException("principal cannot be null");
        }
        if (principal.isEmpty()) {
            throw new IllegalArgumentException("principal cannot be empty");
        }
        if (principal.length() > Validation.PRINCIPAL_MAX_LENGTH) {
            throw new IllegalArgumentException("principal cannot be longer than " + Validation.PRINCIPAL_MAX_LENGTH);
        }
        if (plainCredential == null) {
            throw new IllegalArgumentException("plainCredential cannot be null");
        }
        if (plainCredential.isEmpty()) {
            throw new IllegalArgumentException("plainCredential cannot be empty");
        }
        if (plainCredential.length() > Validation.CREDENTIAL_MAX_LENGTH) {
            throw new IllegalArgumentException("plainCredential cannot be longer than "
                    + Validation.CREDENTIAL_MAX_LENGTH);
        }
        SimpleSubject rval = transactionHelper.required(new Callback<SimpleSubject>() {

            @Override
            public SimpleSubject execute() {
                long usedResourceId;
                if (resourceId == null) {
                    usedResourceId = resourceService.createResource();
                } else {
                    usedResourceId = resourceId;
                }
                String credential = credentialEncryptor.encryptCredential(plainCredential);
                try (Connection connection = dataSource.getConnection()) {
                    QSimpleSubject qSimpleSubject = QSimpleSubject.authcSimpleSubject;
                    long simpleSubjectId = new SQLInsertClause(connection, sqlTemplates, qSimpleSubject)
                            .set(qSimpleSubject.resourceId, usedResourceId)
                            .set(qSimpleSubject.principal, principal)
                            .set(qSimpleSubject.credential, credential)
                            .set(qSimpleSubject.active, active)
                            .executeWithKey(qSimpleSubject.simpleSubjectId);
                    return new SimpleSubject(simpleSubjectId, principal, active, usedResourceId);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        });
        return rval;
    }

    @Override
    public long deactivate(final String principal) {
        return setActive(principal, false);
    }

    @Override
    public long delete(final String principal) {
        if (principal == null) {
            throw new IllegalArgumentException("principal cannot be null");
        }
        QSimpleSubject qSimpleSubject = QSimpleSubject.authcSimpleSubject;
        try (Connection connection = dataSource.getConnection()) {
            long deleteCount = new SQLDeleteClause(connection, sqlTemplates, qSimpleSubject)
                    .where(qSimpleSubject.principal.eq(principal))
                    .execute();
            return deleteCount;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getCredential(final String principal) {
        if (principal == null) {
            throw new IllegalArgumentException("principal cannot be null");
        }
        QSimpleSubject qSimpleSubject = QSimpleSubject.authcSimpleSubject;
        try (Connection connection = dataSource.getConnection()) {
            List<String> credentials = new SQLQuery(connection, sqlTemplates)
                    .from(qSimpleSubject)
                    .where(qSimpleSubject.principal.eq(principal))
                    .limit(1)
                    .list(qSimpleSubject.credential);
            if (credentials.isEmpty()) {
                return null;
            } else {
                return credentials.get(0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isActive(final String principal) {
        if (principal == null) {
            throw new IllegalArgumentException("principal cannot be null");
        }
        QSimpleSubject qSimpleSubject = QSimpleSubject.authcSimpleSubject;
        try (Connection connection = dataSource.getConnection()) {
            long count = new SQLQuery(connection, sqlTemplates)
                    .from(qSimpleSubject)
                    .where(qSimpleSubject.principal.eq(principal).and(qSimpleSubject.active.eq(true)))
                    .count();
            return count != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SimpleSubject readByPrincipal(final String principal) {
        if (principal == null) {
            throw new IllegalArgumentException("principal cannot be null");
        }
        QSimpleSubject qSimpleSubject = QSimpleSubject.authcSimpleSubject;
        try (Connection connection = dataSource.getConnection()) {
            List<SimpleSubject> simpleSubjects = new SQLQuery(connection, sqlTemplates)
                    .from(qSimpleSubject)
                    .where(qSimpleSubject.principal.eq(principal))
                    .limit(1)
                    .list(ConstructorExpression.create(SimpleSubject.class,
                            qSimpleSubject.simpleSubjectId,
                            qSimpleSubject.principal,
                            qSimpleSubject.active,
                            qSimpleSubject.resourceId));
            if (simpleSubjects.isEmpty()) {
                return null;
            } else {
                return simpleSubjects.get(0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long setActive(final String principal, final boolean active) {
        if (principal == null) {
            throw new IllegalArgumentException("principal cannot be null");
        }
        QSimpleSubject qSimpleSubject = QSimpleSubject.authcSimpleSubject;
        try (Connection connection = dataSource.getConnection()) {
            long count = new SQLUpdateClause(connection, sqlTemplates, qSimpleSubject)
                    .where(qSimpleSubject.principal.eq(principal))
                    .set(qSimpleSubject.active, active)
                    .execute();
            return count;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long updatePrincipal(final String principal, final String newPrincipal) {
        if (principal == null) {
            throw new IllegalArgumentException("principal cannot be null");
        }
        if (newPrincipal == null) {
            throw new IllegalArgumentException("newPrincipal cannot be null");
        }
        if (newPrincipal.isEmpty()) {
            throw new IllegalArgumentException("newPrincipal cannot be empty");
        }
        if (newPrincipal.length() > Validation.PRINCIPAL_MAX_LENGTH) {
            throw new IllegalArgumentException("newPrincipal cannot be longer than " + Validation.PRINCIPAL_MAX_LENGTH);
        }
        QSimpleSubject qSimpleSubject = QSimpleSubject.authcSimpleSubject;
        try (Connection connection = dataSource.getConnection()) {
            long count = new SQLUpdateClause(connection, sqlTemplates, qSimpleSubject)
                    .where(qSimpleSubject.principal.eq(principal))
                    .set(qSimpleSubject.principal, newPrincipal)
                    .execute();
            return count;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
