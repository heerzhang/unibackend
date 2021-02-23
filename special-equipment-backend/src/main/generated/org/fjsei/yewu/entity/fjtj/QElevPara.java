package org.fjsei.yewu.entity.fjtj;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QElevPara is a Querydsl query type for ElevPara
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QElevPara extends EntityPathBase<ElevPara> {

    private static final long serialVersionUID = -1090428288L;

    public static final QElevPara elevPara = new QElevPara("elevPara");

    public final StringPath CONTROL_TYPE = createString("CONTROL_TYPE");

    public final NumberPath<Long> ELEDOORNUMBER = createNumber("ELEDOORNUMBER", Long.class);

    public final NumberPath<Long> ELEFLOORNUMBER = createNumber("ELEFLOORNUMBER", Long.class);

    public final NumberPath<Long> ELESTADENUMBER = createNumber("ELESTADENUMBER", Long.class);

    public final StringPath eqpcod = createString("eqpcod");

    public final NumberPath<Double> RATEDLOAD = createNumber("RATEDLOAD", Double.class);

    public final NumberPath<Double> RUNVELOCITY = createNumber("RUNVELOCITY", Double.class);

    public QElevPara(String variable) {
        super(ElevPara.class, forVariable(variable));
    }

    public QElevPara(Path<? extends ElevPara> path) {
        super(path.getType(), path.getMetadata());
    }

    public QElevPara(PathMetadata metadata) {
        super(ElevPara.class, metadata);
    }

}

