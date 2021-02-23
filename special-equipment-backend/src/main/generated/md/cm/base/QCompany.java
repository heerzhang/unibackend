package md.cm.base;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCompany is a Querydsl query type for Company
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCompany extends EntityPathBase<Company> {

    private static final long serialVersionUID = 1652458221L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCompany company = new QCompany("company");

    public final StringPath address = createString("address");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath linkMen = createString("linkMen");

    public final StringPath name = createString("name");

    public final StringPath no = createString("no");

    public final StringPath phone = createString("phone");

    public final md.cm.geography.QAddress pos;

    public QCompany(String variable) {
        this(Company.class, forVariable(variable), INITS);
    }

    public QCompany(Path<? extends Company> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCompany(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCompany(PathMetadata metadata, PathInits inits) {
        this(Company.class, metadata, inits);
    }

    public QCompany(Class<? extends Company> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.pos = inits.isInitialized("pos") ? new md.cm.geography.QAddress(forProperty("pos"), inits.get("pos")) : null;
    }

}

