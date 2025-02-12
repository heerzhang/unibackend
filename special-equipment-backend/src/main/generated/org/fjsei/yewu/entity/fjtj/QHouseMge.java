package org.fjsei.yewu.entity.fjtj;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QHouseMge is a Querydsl query type for HouseMge
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QHouseMge extends EntityPathBase<HouseMge> {

    private static final long serialVersionUID = -1333518925L;

    public static final QHouseMge houseMge = new QHouseMge("houseMge");

    public final StringPath AREA_COD = createString("AREA_COD");

    public final StringPath BUILD_ADDR = createString("BUILD_ADDR");

    public final StringPath BUILD_NAME = createString("BUILD_NAME");

    public final StringPath BUILD_STATE = createString("BUILD_STATE");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath INST_BUILD_TYPE = createString("INST_BUILD_TYPE");

    public QHouseMge(String variable) {
        super(HouseMge.class, forVariable(variable));
    }

    public QHouseMge(Path<? extends HouseMge> path) {
        super(path.getType(), path.getMetadata());
    }

    public QHouseMge(PathMetadata metadata) {
        super(HouseMge.class, metadata);
    }

}

