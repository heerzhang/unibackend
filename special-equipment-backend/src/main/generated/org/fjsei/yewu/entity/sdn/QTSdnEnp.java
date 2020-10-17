package org.fjsei.yewu.entity.sdn;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTSdnEnp is a Querydsl query type for TSdnEnp
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTSdnEnp extends EntityPathBase<TSdnEnp> {

    private static final long serialVersionUID = 308465345L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTSdnEnp tSdnEnp = new QTSdnEnp("tSdnEnp");

    public final StringPath aptitudeFileId = createString("aptitudeFileId");

    public final StringPath certSerial = createString("certSerial");

    public final DatePath<java.sql.Date> createDate = createDate("createDate", java.sql.Date.class);

    public final StringPath createUserId = createString("createUserId");

    public final NumberPath<Long> enpId = createNumber("enpId", Long.class);

    public final StringPath lkmanMobil = createString("lkmanMobil");

    public final StringPath oldCertSerial = createString("oldCertSerial");

    public final StringPath status = createString("status");

    public final QTEbmUser tebmuser;

    public final StringPath untCod = createString("untCod");

    public final StringPath untLkman = createString("untLkman");

    public final StringPath untName = createString("untName");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QTSdnEnp(String variable) {
        this(TSdnEnp.class, forVariable(variable), INITS);
    }

    public QTSdnEnp(Path<? extends TSdnEnp> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTSdnEnp(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTSdnEnp(PathMetadata metadata, PathInits inits) {
        this(TSdnEnp.class, metadata, inits);
    }

    public QTSdnEnp(Class<? extends TSdnEnp> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.tebmuser = inits.isInitialized("tebmuser") ? new QTEbmUser(forProperty("tebmuser"), inits.get("tebmuser")) : null;
    }

}

