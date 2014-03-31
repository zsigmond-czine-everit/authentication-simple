/**
 * This file is part of org.everit.osgi.authentication.simple.schema.
 *
 * org.everit.osgi.authentication.simple.schema is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * org.everit.osgi.authentication.simple.schema is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.everit.osgi.authentication.simple.schema.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.authentication.simple.schema.qdsl;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;




/**
 * QSimpleSubject is a Querydsl query type for QSimpleSubject
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QSimpleSubject extends com.mysema.query.sql.RelationalPathBase<QSimpleSubject> {

    private static final long serialVersionUID = -1415729839;

    public static final QSimpleSubject authcSimpleSubject = new QSimpleSubject("authc_simple_subject");

    public final BooleanPath active = createBoolean("active");

    public final StringPath credential = createString("credential");

    public final StringPath principal = createString("principal");

    public final NumberPath<Long> resourceId = createNumber("resourceId", Long.class);

    public final NumberPath<Long> simpleSubjectId = createNumber("simpleSubjectId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QSimpleSubject> authcSimpleSubjectPk = createPrimaryKey(simpleSubjectId);

    public final com.mysema.query.sql.ForeignKey<org.everit.osgi.resource.schema.qdsl.QResource> simpleSubjectResourceFk = createForeignKey(resourceId, "resource_id");

    public QSimpleSubject(String variable) {
        super(QSimpleSubject.class, forVariable(variable), null, "authc_simple_subject");
        addMetadata();
    }

    public QSimpleSubject(String variable, String schema, String table) {
        super(QSimpleSubject.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QSimpleSubject(Path<? extends QSimpleSubject> path) {
        super(path.getType(), path.getMetadata(), null, "authc_simple_subject");
        addMetadata();
    }

    public QSimpleSubject(PathMetadata<?> metadata) {
        super(QSimpleSubject.class, metadata, null, "authc_simple_subject");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(active, ColumnMetadata.named("active").ofType(16).withSize(1).notNull());
        addMetadata(credential, ColumnMetadata.named("credential").ofType(12).withSize(255).notNull());
        addMetadata(principal, ColumnMetadata.named("principal").ofType(12).withSize(255).notNull());
        addMetadata(resourceId, ColumnMetadata.named("resource_id").ofType(-5).withSize(19).notNull());
        addMetadata(simpleSubjectId, ColumnMetadata.named("simple_subject_id").ofType(-5).withSize(19).notNull());
    }

}

