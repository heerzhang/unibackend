package org.fjsei.yewu.entity.incp;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QJcAuthor is a Querydsl query type for JcAuthor
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QJcAuthor extends EntityPathBase<JcAuthor> {

    private static final long serialVersionUID = 1841006580L;

    public static final QJcAuthor jcAuthor = new QJcAuthor("jcAuthor");

    public final SetPath<JcBook, QJcBook> books = this.<JcBook, QJcBook>createSet("books", JcBook.class, QJcBook.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath nickname = createString("nickname");

    public QJcAuthor(String variable) {
        super(JcAuthor.class, forVariable(variable));
    }

    public QJcAuthor(Path<? extends JcAuthor> path) {
        super(path.getType(), path.getMetadata());
    }

    public QJcAuthor(PathMetadata metadata) {
        super(JcAuthor.class, metadata);
    }

}

