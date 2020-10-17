package org.fjsei.yewu.repository;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QTbUntMge is a Querydsl query type for TbUntMge
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTbUntMge extends EntityPathBase<TbUntMge> {

    private static final long serialVersionUID = 238633251L;

    public static final QTbUntMge tbUntMge = new QTbUntMge("tbUntMge");

    public final DatePath<java.sql.Date> addDate = createDate("addDate", java.sql.Date.class);

    public final StringPath addPlace = createString("addPlace");

    public final StringPath addUserId = createString("addUserId");

    public final StringPath busilicnum = createString("busilicnum");

    public final StringPath calisCod = createString("calisCod");

    public final StringPath econmTypeCod = createString("econmTypeCod");

    public final StringPath email = createString("email");

    public final StringPath ifMatchjc = createString("ifMatchjc");

    public final StringPath ifQhunt = createString("ifQhunt");

    public final StringPath ifWtunt = createString("ifWtunt");

    public final StringPath industryPropCod = createString("industryPropCod");

    public final StringPath jcUntId = createString("jcUntId");

    public final DatePath<java.sql.Date> keyuntEffdate = createDate("keyuntEffdate", java.sql.Date.class);

    public final StringPath keyuntReasonType = createString("keyuntReasonType");

    public final StringPath keyuntType = createString("keyuntType");

    public final StringPath leader = createString("leader");

    public final StringPath leaderDuty = createString("leaderDuty");

    public final StringPath leaderPhone = createString("leaderPhone");

    public final StringPath manageUnt = createString("manageUnt");

    public final StringPath memo = createString("memo");

    public final StringPath oldUntId = createString("oldUntId");

    public final StringPath postCod = createString("postCod");

    public final StringPath sourceType = createString("sourceType");

    public final StringPath untAddr = createString("untAddr");

    public final StringPath untAreaCod = createString("untAreaCod");

    public final StringPath untCerdit = createString("untCerdit");

    public final StringPath untFfass = createString("untFfass");

    public final StringPath untFox = createString("untFox");

    public final StringPath untId = createString("untId");

    public final StringPath untLat = createString("untLat");

    public final StringPath untLkmen = createString("untLkmen");

    public final StringPath untLong = createString("untLong");

    public final StringPath untMobile = createString("untMobile");

    public final StringPath untName = createString("untName");

    public final StringPath untOrgCod = createString("untOrgCod");

    public final StringPath untPhone = createString("untPhone");

    public final StringPath untPropCod = createString("untPropCod");

    public final StringPath untSealev = createString("untSealev");

    public final DatePath<java.sql.Date> untStachgDate = createDate("untStachgDate", java.sql.Date.class);

    public final StringPath untState = createString("untState");

    public final DatePath<java.sql.Date> updateDate = createDate("updateDate", java.sql.Date.class);

    public final StringPath updateUserId = createString("updateUserId");

    public final StringPath webAddr = createString("webAddr");

    public QTbUntMge(String variable) {
        super(TbUntMge.class, forVariable(variable));
    }

    public QTbUntMge(Path<? extends TbUntMge> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTbUntMge(PathMetadata metadata) {
        super(TbUntMge.class, metadata);
    }

}

