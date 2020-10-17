package md.cm.geography;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAdminunit is a Querydsl query type for Adminunit
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QAdminunit extends EntityPathBase<Adminunit> {

    private static final long serialVersionUID = -389302284L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAdminunit adminunit = new QAdminunit("adminunit");

    public final SetPath<Address, QAddress> adrs = this.<Address, QAddress>createSet("adrs", Address.class, QAddress.class, PathInits.DIRECT2);

    public final StringPath areacode = createString("areacode");

    public final QCity city;

    public final QCountry country;

    public final QCounty county;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath prefix = createString("prefix");

    public final QProvince province;

    public final QTown town;

    public final StringPath zipcode = createString("zipcode");

    public QAdminunit(String variable) {
        this(Adminunit.class, forVariable(variable), INITS);
    }

    public QAdminunit(Path<? extends Adminunit> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAdminunit(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAdminunit(PathMetadata metadata, PathInits inits) {
        this(Adminunit.class, metadata, inits);
    }

    public QAdminunit(Class<? extends Adminunit> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.city = inits.isInitialized("city") ? new QCity(forProperty("city"), inits.get("city")) : null;
        this.country = inits.isInitialized("country") ? new QCountry(forProperty("country")) : null;
        this.county = inits.isInitialized("county") ? new QCounty(forProperty("county"), inits.get("county")) : null;
        this.province = inits.isInitialized("province") ? new QProvince(forProperty("province"), inits.get("province")) : null;
        this.town = inits.isInitialized("town") ? new QTown(forProperty("town"), inits.get("town")) : null;
    }

}

