package org.fjsei.yewu.entity.fjtj;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QUntSecudept is a Querydsl query type for UntSecudept
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QUntSecudept extends EntityPathBase<UntSecudept> {

    private static final long serialVersionUID = -1883851588L;

    public static final QUntSecudept untSecudept = new QUntSecudept("untSecudept");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath LKMEN = createString("LKMEN");

    public final StringPath MOBILE = createString("MOBILE");

    public final StringPath NAME = createString("NAME");

    public final StringPath PHONE = createString("PHONE");

    public final StringPath SECUDEPT_ADDR = createString("SECUDEPT_ADDR");

    public QUntSecudept(String variable) {
        super(UntSecudept.class, forVariable(variable));
    }

    public QUntSecudept(Path<? extends UntSecudept> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUntSecudept(PathMetadata metadata) {
        super(UntSecudept.class, metadata);
    }

}

