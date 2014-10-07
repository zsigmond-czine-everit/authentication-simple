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
package org.everit.osgi.authentication.simple.internal;

import java.util.Optional;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.everit.osgi.authentication.simple.AuthenticationSimpleConstants;
import org.everit.osgi.authentication.simple.SimpleSubject;
import org.everit.osgi.authentication.simple.SimpleSubjectManager;
import org.everit.osgi.authentication.simple.schema.qdsl.QSimpleSubject;
import org.everit.osgi.authenticator.Authenticator;
import org.everit.osgi.credential.encryptor.CredentialEncryptor;
import org.everit.osgi.credential.encryptor.CredentialMatcher;
import org.everit.osgi.querydsl.support.QuerydslSupport;
import org.everit.osgi.resource.resolver.ResourceIdResolver;
import org.osgi.framework.Constants;
import org.osgi.service.log.LogService;

import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.dml.SQLDeleteClause;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.types.ConstructorExpression;

@Component(name = AuthenticationSimpleConstants.SERVICE_FACTORYPID_AUTHENTICATION_SIMPLE, metatype = true,
        configurationFactory = true, policy = ConfigurationPolicy.REQUIRE)
@Properties({
        @Property(name = Constants.SERVICE_DESCRIPTION, propertyPrivate = false,
                value = AuthenticationSimpleConstants.DEFAULT_SERVICE_DESCRIPTION),
        @Property(name = AuthenticationSimpleConstants.PROP_QUERYDSL_SUPPORT),
        @Property(name = AuthenticationSimpleConstants.PROP_CREDENTIAL_ENCRYPTOR),
        @Property(name = AuthenticationSimpleConstants.PROP_CREDENTIAL_MATCHER),
        @Property(name = AuthenticationSimpleConstants.PROP_LOG_SERVICE)
})
@Service
public class AuthenticationSimpleComponent implements SimpleSubjectManager, Authenticator, ResourceIdResolver {

    @Reference(bind = "setQuerydslSupport")
    private QuerydslSupport querydslSupport;

    @Reference(bind = "setCredentialEncryptor")
    private CredentialEncryptor credentialEncryptor;

    @Reference(bind = "setCredentialMatcher")
    private CredentialMatcher credentialMatcher;

    @Reference(bind = "setLogService")
    private LogService logService;

    @Override
    public Optional<String> authenticate(final String principal, final String credential) {
        if ((principal == null) || (credential == null)) {
            return Optional.empty();
        }
        String encryptedCredential = readEncryptedCredential(principal);
        if (encryptedCredential == null) {
            return Optional.empty();
        }
        boolean match = credentialMatcher.match(credential, encryptedCredential);
        if (match) {
            logService.log(LogService.LOG_INFO, "Successfully authenticated '" + principal + "'");
            return Optional.of(principal);
        } else {
            logService.log(LogService.LOG_INFO, "Failed to authenticate '" + principal + "'");
            return Optional.empty();
        }
    }

    @Override
    public SimpleSubject create(final long resourceId, final String principal, final String plainCredential) {
        String encryptedCredential = (plainCredential == null) ? null
                : credentialEncryptor.encrypt(plainCredential);

        return querydslSupport.execute((connection, configuration) -> {
            QSimpleSubject qSimpleSubject = QSimpleSubject.simpleSubject;
            long simpleSubjectId = new SQLInsertClause(connection, configuration, qSimpleSubject)
                    .set(qSimpleSubject.resourceId, resourceId)
                    .set(qSimpleSubject.principal, principal)
                    .set(qSimpleSubject.encryptedCredential, encryptedCredential)
                    .executeWithKey(qSimpleSubject.simpleSubjectId);
            return new SimpleSubject(simpleSubjectId, principal, resourceId);
        });
    }

    @Override
    public boolean delete(final String principal) {
        long deleteCount = querydslSupport.execute((connection, configuration) -> {
            QSimpleSubject qSimpleSubject = QSimpleSubject.simpleSubject;
            return new SQLDeleteClause(connection, configuration, qSimpleSubject)
                    .where(qSimpleSubject.principal.eq(principal))
                    .execute();
        });
        return deleteCount > 0;
    }

    @Override
    public Optional<Long> getResourceId(final String principal) {
        return querydslSupport.execute((connection, configuration) -> {
            QSimpleSubject qSimpleSubject = QSimpleSubject.simpleSubject;
            Long resourceId = new SQLQuery(connection, configuration)
                    .from(qSimpleSubject)
                    .where(qSimpleSubject.principal.eq(principal))
                    .singleResult(qSimpleSubject.resourceId);
            return Optional.ofNullable(resourceId);
        });
    }

    @Override
    public String readEncryptedCredential(final String principal) {
        return querydslSupport.execute((connection, configuration) -> {
            QSimpleSubject qSimpleSubject = QSimpleSubject.simpleSubject;
            return new SQLQuery(connection, configuration)
                    .from(qSimpleSubject)
                    .where(qSimpleSubject.principal.eq(principal))
                    .singleResult(qSimpleSubject.encryptedCredential);
        });
    }

    @Override
    public SimpleSubject readSimpleSubjectByPrincipal(final String principal) {
        return querydslSupport.execute((connection, configuration) -> {
            QSimpleSubject qSimpleSubject = QSimpleSubject.simpleSubject;
            return new SQLQuery(connection, configuration)
                    .from(qSimpleSubject)
                    .where(qSimpleSubject.principal.eq(principal))
                    .singleResult(ConstructorExpression.create(SimpleSubject.class,
                            qSimpleSubject.simpleSubjectId,
                            qSimpleSubject.principal,
                            qSimpleSubject.resourceId));
        });
    }

    public void setCredentialEncryptor(final CredentialEncryptor credentialEncryptor) {
        this.credentialEncryptor = credentialEncryptor;
    }

    public void setCredentialMatcher(final CredentialMatcher credentialMatcher) {
        this.credentialMatcher = credentialMatcher;
    }

    public void setLogService(final LogService logService) {
        this.logService = logService;
    }

    public void setQuerydslSupport(final QuerydslSupport querydslSupport) {
        this.querydslSupport = querydslSupport;
    }

    @Override
    public boolean updateCredential(final String principal, final String newPlainCredential) {
        String encryptedCredential = (newPlainCredential == null) ? null
                : credentialEncryptor.encrypt(newPlainCredential);

        long updateCount = querydslSupport.execute((connection, configuration) -> {
            QSimpleSubject qSimpleSubject = QSimpleSubject.simpleSubject;
            return new SQLUpdateClause(connection, configuration, qSimpleSubject)
                    .where(qSimpleSubject.principal.eq(principal))
                    .set(qSimpleSubject.encryptedCredential, encryptedCredential)
                    .execute();
        });
        return updateCount > 0;
    }

    @Override
    public boolean updateCredential(final String principal, final String originalPlainCredential,
            final String newPlainCredential) {

        QSimpleSubject qSimpleSubject = QSimpleSubject.simpleSubject;

        String originalEncryptedCredential = readEncryptedCredential(principal);
        if ((originalEncryptedCredential != null)
                && !credentialMatcher.match(originalPlainCredential, originalEncryptedCredential)) {
            return false;
        }

        String encryptedNewCredential = (newPlainCredential == null) ? null
                : credentialEncryptor.encrypt(newPlainCredential);

        long updateCount = querydslSupport.execute((connection, configuration) -> {
            return new SQLUpdateClause(connection, configuration, qSimpleSubject)
                    .where(qSimpleSubject.principal.eq(principal))
                    .set(qSimpleSubject.encryptedCredential, encryptedNewCredential)
                    .execute();
        });
        return updateCount > 0;
    }

    @Override
    public boolean updatePrincipal(final String principal, final String newPrincipal) {
        long updateCount = querydslSupport.execute((connection, configuration) -> {
            QSimpleSubject qSimpleSubject = QSimpleSubject.simpleSubject;
            return new SQLUpdateClause(connection, configuration, qSimpleSubject)
                    .where(qSimpleSubject.principal.eq(principal))
                    .set(qSimpleSubject.principal, newPrincipal)
                    .execute();
        });
        return updateCount > 0;
    }

}
