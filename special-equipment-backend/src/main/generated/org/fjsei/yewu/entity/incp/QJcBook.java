package org.fjsei.yewu.entity.incp;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QJcBook is a Querydsl query type for JcBook
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QJcBook extends EntityPathBase<JcBook> {

    private static final long serialVersionUID = 1271211922L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QJcBook jcBook = new QJcBook("jcBook");

    public final QJcAuthor author;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath isbn = createString("isbn");

    public final StringPath title = createString("title");

    public QJcBook(String variable) {
        this(JcBook.class, forVariable(variable), INITS);
    }

    public QJcBook(Path<? extends JcBook> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QJcBook(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QJcBook(PathMetadata metadata, PathInits inits) {
        this(JcBook.class, metadata, inits);
    }

    public QJcBook(Class<? extends JcBook> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new QJcAuthor(forProperty("author")) : null;
    }

}

