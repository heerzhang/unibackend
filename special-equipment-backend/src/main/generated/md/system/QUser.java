package md.system;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 1514686067L;

    public static final QUser user = new QUser("user");

    public final StringPath authName = createString("authName");

    public final SetPath<Authority, QAuthority> authorities = this.<Authority, QAuthority>createSet("authorities", Authority.class, QAuthority.class, PathInits.DIRECT2);

    public final StringPath authType = createString("authType");

    public final SetPath<md.julienne.Following, md.julienne.QFollowing> beFollowed = this.<md.julienne.Following, md.julienne.QFollowing>createSet("beFollowed", md.julienne.Following.class, md.julienne.QFollowing.class, PathInits.DIRECT2);

    public final SetPath<md.specialEqp.inspect.ISP, md.specialEqp.inspect.QISP> checks = this.<md.specialEqp.inspect.ISP, md.specialEqp.inspect.QISP>createSet("checks", md.specialEqp.inspect.ISP.class, md.specialEqp.inspect.QISP.class, PathInits.DIRECT2);

    public final StringPath dep = createString("dep");

    public final StringPath email = createString("email");

    public final BooleanPath enabled = createBoolean("enabled");

    public final StringPath firstname = createString("firstname");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final SetPath<md.julienne.Following, md.julienne.QFollowing> iFollowing = this.<md.julienne.Following, md.julienne.QFollowing>createSet("iFollowing", md.julienne.Following.class, md.julienne.QFollowing.class, PathInits.DIRECT2);

    public final SetPath<md.specialEqp.inspect.ISP, md.specialEqp.inspect.QISP> isp = this.<md.specialEqp.inspect.ISP, md.specialEqp.inspect.QISP>createSet("isp", md.specialEqp.inspect.ISP.class, md.specialEqp.inspect.QISP.class, PathInits.DIRECT2);

    public final StringPath lastname = createString("lastname");

    public final DateTimePath<java.util.Date> lastPasswordResetDate = createDateTime("lastPasswordResetDate", java.util.Date.class);

    public final StringPath mobile = createString("mobile");

    public final StringPath password = createString("password");

    public final StringPath photoURL = createString("photoURL");

    public final StringPath username = createString("username");

    public final StringPath 旧账户 = createString("旧账户");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

