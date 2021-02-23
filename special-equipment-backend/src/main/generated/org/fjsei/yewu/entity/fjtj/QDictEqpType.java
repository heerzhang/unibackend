package org.fjsei.yewu.entity.fjtj;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QDictEqpType is a Querydsl query type for DictEqpType
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QDictEqpType extends EntityPathBase<DictEqpType> {

    private static final long serialVersionUID = -312341984L;

    public static final QDictEqpType dictEqpType = new QDictEqpType("dictEqpType");

    public final StringPath CLASS_NAME = createString("CLASS_NAME");

    public final StringPath idCod = createString("idCod");

    public QDictEqpType(String variable) {
        super(DictEqpType.class, forVariable(variable));
    }

    public QDictEqpType(Path<? extends DictEqpType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDictEqpType(PathMetadata metadata) {
        super(DictEqpType.class, metadata);
    }

}

