package org.fjsei.yewu.entity.fjtj;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QUntDept is a Querydsl query type for UntDept
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QUntDept extends EntityPathBase<UntDept> {

    private static final long serialVersionUID = -590038536L;

    public static final QUntDept untDept = new QUntDept("untDept");

    public final StringPath DEPT_ADDR = createString("DEPT_ADDR");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath LKMEN = createString("LKMEN");

    public final StringPath MOBILE = createString("MOBILE");

    public final StringPath NAME = createString("NAME");

    public final StringPath PHONE = createString("PHONE");

    public QUntDept(String variable) {
        super(UntDept.class, forVariable(variable));
    }

    public QUntDept(Path<? extends UntDept> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUntDept(PathMetadata metadata) {
        super(UntDept.class, metadata);
    }

}

