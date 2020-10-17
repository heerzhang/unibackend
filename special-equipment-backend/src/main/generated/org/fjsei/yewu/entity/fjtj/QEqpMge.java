package org.fjsei.yewu.entity.fjtj;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QEqpMge is a Querydsl query type for EqpMge
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QEqpMge extends EntityPathBase<EqpMge> {

    private static final long serialVersionUID = 495391503L;

    public static final QEqpMge eqpMge = new QEqpMge("eqpMge");

    public final DatePath<java.util.Date> ALT_DATE = createDate("ALT_DATE", java.util.Date.class);

    public final NumberPath<Long> ALT_UNT_ID = createNumber("ALT_UNT_ID", Long.class);

    public final NumberPath<Long> BUILD_ID = createNumber("BUILD_ID", Long.class);

    public final StringPath EQP_INNER_COD = createString("EQP_INNER_COD");

    public final StringPath EQP_MOD = createString("EQP_MOD");

    public final StringPath EQP_NAME = createString("EQP_NAME");

    public final StringPath EQP_SORT = createString("EQP_SORT");

    public final StringPath EQP_SORT_NAME = createString("EQP_SORT_NAME");

    public final StringPath EQP_STATION_COD = createString("EQP_STATION_COD");

    public final StringPath EQP_USE_ADDR = createString("EQP_USE_ADDR");

    public final StringPath EQP_USECERT_COD = createString("EQP_USECERT_COD");

    public final StringPath EQP_VART = createString("EQP_VART");

    public final StringPath EQP_VART_NAME = createString("EQP_VART_NAME");

    public final StringPath eqpcod = createString("eqpcod");

    public final StringPath FACTORY_COD = createString("FACTORY_COD");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DatePath<java.util.Date> MAKE_DATE = createDate("MAKE_DATE", java.util.Date.class);

    public final NumberPath<Long> MAKE_UNT_ID = createNumber("MAKE_UNT_ID", Long.class);

    public final NumberPath<Long> MANT_UNT_ID = createNumber("MANT_UNT_ID", Long.class);

    public final DatePath<java.util.Date> NEXT_ISP_DATE1 = createDate("NEXT_ISP_DATE1", java.util.Date.class);

    public final DatePath<java.util.Date> NEXT_ISP_DATE2 = createDate("NEXT_ISP_DATE2", java.util.Date.class);

    public final StringPath OIDNO = createString("OIDNO");

    public final NumberPath<Long> SECUDEPT_ID = createNumber("SECUDEPT_ID", Long.class);

    public final StringPath USE_UNT_ADDR = createString("USE_UNT_ADDR");

    public final NumberPath<Long> USE_UNT_ID = createNumber("USE_UNT_ID", Long.class);

    public QEqpMge(String variable) {
        super(EqpMge.class, forVariable(variable));
    }

    public QEqpMge(Path<? extends EqpMge> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEqpMge(PathMetadata metadata) {
        super(EqpMge.class, metadata);
    }

}

