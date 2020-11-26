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

    public final DateTimePath<java.util.Date> COMPE_ACCP_DATE = createDateTime("COMPE_ACCP_DATE", java.util.Date.class);

    public final DateTimePath<java.util.Date> DESIGN_USE_OVERYEAR = createDateTime("DESIGN_USE_OVERYEAR", java.util.Date.class);

    public final StringPath EQP_AREA_COD = createString("EQP_AREA_COD");

    public final StringPath EQP_INNER_COD = createString("EQP_INNER_COD");

    public final NumberPath<Double> EQP_LAT = createNumber("EQP_LAT", Double.class);

    public final StringPath EQP_LEVEL = createString("EQP_LEVEL");

    public final NumberPath<Double> EQP_LONG = createNumber("EQP_LONG", Double.class);

    public final StringPath EQP_MOD = createString("EQP_MOD");

    public final StringPath EQP_NAME = createString("EQP_NAME");

    public final NumberPath<Float> EQP_PRICE = createNumber("EQP_PRICE", Float.class);

    public final StringPath EQP_REG_COD = createString("EQP_REG_COD");

    public final ComparablePath<Character> EQP_REG_STA = createComparable("EQP_REG_STA", Character.class);

    public final StringPath EQP_SORT = createString("EQP_SORT");

    public final StringPath EQP_STATION_COD = createString("EQP_STATION_COD");

    public final StringPath EQP_TYPE = createString("EQP_TYPE");

    public final StringPath EQP_USE_ADDR = createString("EQP_USE_ADDR");

    public final StringPath EQP_USE_OCCA = createString("EQP_USE_OCCA");

    public final ComparablePath<Character> EQP_USE_STA = createComparable("EQP_USE_STA", Character.class);

    public final StringPath EQP_USECERT_COD = createString("EQP_USECERT_COD");

    public final StringPath EQP_VART = createString("EQP_VART");

    public final StringPath eqpcod = createString("eqpcod");

    public final StringPath FACTORY_COD = createString("FACTORY_COD");

    public final DateTimePath<java.util.Date> FIRSTUSE_DATE = createDateTime("FIRSTUSE_DATE", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ComparablePath<Character> IF_INCPING = createComparable("IF_INCPING", Character.class);

    public final StringPath IF_MAJEQP = createString("IF_MAJEQP");

    public final ComparablePath<Character> IN_CAG = createComparable("IN_CAG", Character.class);

    public final NumberPath<Long> INST_UNT_ID = createNumber("INST_UNT_ID", Long.class);

    public final ComparablePath<Character> IS_MOVEEQP = createComparable("IS_MOVEEQP", Character.class);

    public final StringPath LAST_ISP_CONCLU1 = createString("LAST_ISP_CONCLU1");

    public final StringPath LAST_ISP_CONCLU2 = createString("LAST_ISP_CONCLU2");

    public final DateTimePath<java.util.Date> LAST_ISP_DATE1 = createDateTime("LAST_ISP_DATE1", java.util.Date.class);

    public final DateTimePath<java.util.Date> LAST_ISP_DATE2 = createDateTime("LAST_ISP_DATE2", java.util.Date.class);

    public final DatePath<java.util.Date> MAKE_DATE = createDate("MAKE_DATE", java.util.Date.class);

    public final NumberPath<Long> MAKE_UNT_ID = createNumber("MAKE_UNT_ID", Long.class);

    public final NumberPath<Long> MANT_DEPT_ID = createNumber("MANT_DEPT_ID", Long.class);

    public final NumberPath<Long> MANT_UNT_ID = createNumber("MANT_UNT_ID", Long.class);

    public final NumberPath<Byte> MGE_DEPT_TYPE = createNumber("MGE_DEPT_TYPE", Byte.class);

    public final DatePath<java.util.Date> NEXT_ISP_DATE1 = createDate("NEXT_ISP_DATE1", java.util.Date.class);

    public final DatePath<java.util.Date> NEXT_ISP_DATE2 = createDate("NEXT_ISP_DATE2", java.util.Date.class);

    public final BooleanPath NOTELIGIBLE_FALG1 = createBoolean("NOTELIGIBLE_FALG1");

    public final BooleanPath NOTELIGIBLE_FALG2 = createBoolean("NOTELIGIBLE_FALG2");

    public final StringPath OIDNO = createString("OIDNO");

    public final NumberPath<Long> PROP_UNT_ID = createNumber("PROP_UNT_ID", Long.class);

    public final NumberPath<Long> REG_UNT_ID = createNumber("REG_UNT_ID", Long.class);

    public final NumberPath<Long> SAFE_DEPT_ID = createNumber("SAFE_DEPT_ID", Long.class);

    public final NumberPath<Long> SECUDEPT_ID = createNumber("SECUDEPT_ID", Long.class);

    public final StringPath SUB_EQP_VART = createString("SUB_EQP_VART");

    public final StringPath USE_MOBILE = createString("USE_MOBILE");

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

