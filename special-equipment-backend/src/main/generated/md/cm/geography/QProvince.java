package md.cm.geography;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProvince is a Querydsl query type for Province
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QProvince extends EntityPathBase<Province> {

    private static final long serialVersionUID = 2047260783L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProvince province = new QProvince("province");

    public final SetPath<Adminunit, QAdminunit> ads = this.<Adminunit, QAdminunit>createSet("ads", Adminunit.class, QAdminunit.class, PathInits.DIRECT2);

    public final SetPath<City, QCity> cities = this.<City, QCity>createSet("cities", City.class, QCity.class, PathInits.DIRECT2);

    public final BooleanPath collapse = createBoolean("collapse");

    public final QCountry country;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<Long> oldId = createNumber("oldId", Long.class);

    public QProvince(String variable) {
        this(Province.class, forVariable(variable), INITS);
    }

    public QProvince(Path<? extends Province> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProvince(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProvince(PathMetadata metadata, PathInits inits) {
        this(Province.class, metadata, inits);
    }

    public QProvince(Class<? extends Province> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.country = inits.isInitialized("country") ? new QCountry(forProperty("country")) : null;
    }

}

