package org.fjsei.yewu.entity.incp;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QJcOpemen is a Querydsl query type for JcOpemen
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QJcOpemen extends EntityPathBase<JcOpemen> {

    private static final long serialVersionUID = -2058212581L;

    public static final QJcOpemen jcOpemen = new QJcOpemen("jcOpemen");

    public final StringPath idCard = createString("idCard");

    public final StringPath mobile = createString("mobile");

    public final StringPath name = createString("name");

    public final StringPath userId = createString("userId");

    public QJcOpemen(String variable) {
        super(JcOpemen.class, forVariable(variable));
    }

    public QJcOpemen(Path<? extends JcOpemen> path) {
        super(path.getType(), path.getMetadata());
    }

    public QJcOpemen(PathMetadata metadata) {
        super(JcOpemen.class, metadata);
    }

}

