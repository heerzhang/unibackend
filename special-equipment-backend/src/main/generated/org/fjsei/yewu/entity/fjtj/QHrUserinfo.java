package org.fjsei.yewu.entity.fjtj;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QHrUserinfo is a Querydsl query type for HrUserinfo
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QHrUserinfo extends EntityPathBase<HrUserinfo> {

    private static final long serialVersionUID = 1655024395L;

    public static final QHrUserinfo hrUserinfo = new QHrUserinfo("hrUserinfo");

    public final StringPath password = createString("password");

    public final NumberPath<Integer> status = createNumber("status", Integer.class);

    public final StringPath userId = createString("userId");

    public QHrUserinfo(String variable) {
        super(HrUserinfo.class, forVariable(variable));
    }

    public QHrUserinfo(Path<? extends HrUserinfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QHrUserinfo(PathMetadata metadata) {
        super(HrUserinfo.class, metadata);
    }

}

