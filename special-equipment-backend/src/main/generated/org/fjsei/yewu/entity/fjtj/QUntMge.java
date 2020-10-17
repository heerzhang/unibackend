package org.fjsei.yewu.entity.fjtj;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QUntMge is a Querydsl query type for UntMge
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QUntMge extends EntityPathBase<UntMge> {

    private static final long serialVersionUID = 950806520L;

    public static final QUntMge untMge = new QUntMge("untMge");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath POST_COD = createString("POST_COD");

    public final StringPath UNT_ADDR = createString("UNT_ADDR");

    public final StringPath UNT_LKMEN = createString("UNT_LKMEN");

    public final StringPath UNT_MOBILE = createString("UNT_MOBILE");

    public final StringPath UNT_NAME = createString("UNT_NAME");

    public final StringPath UNT_ORG_COD = createString("UNT_ORG_COD");

    public QUntMge(String variable) {
        super(UntMge.class, forVariable(variable));
    }

    public QUntMge(Path<? extends UntMge> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUntMge(PathMetadata metadata) {
        super(UntMge.class, metadata);
    }

}

