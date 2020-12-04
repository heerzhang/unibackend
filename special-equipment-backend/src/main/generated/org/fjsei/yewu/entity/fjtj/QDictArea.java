package org.fjsei.yewu.entity.fjtj;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QDictArea is a Querydsl query type for DictArea
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QDictArea extends EntityPathBase<DictArea> {

    private static final long serialVersionUID = -1260318645L;

    public static final QDictArea dictArea = new QDictArea("dictArea");

    public final StringPath FAU_TYPE_CODE = createString("FAU_TYPE_CODE");

    public final StringPath FAU_TYPE_NAME = createString("FAU_TYPE_NAME");

    public final NumberPath<Long> FAU_TYPE_PARENT_CODE = createNumber("FAU_TYPE_PARENT_CODE", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QDictArea(String variable) {
        super(DictArea.class, forVariable(variable));
    }

    public QDictArea(Path<? extends DictArea> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDictArea(PathMetadata metadata) {
        super(DictArea.class, metadata);
    }

}

