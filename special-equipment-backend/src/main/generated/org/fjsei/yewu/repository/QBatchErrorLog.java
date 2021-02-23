package org.fjsei.yewu.repository;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QBatchErrorLog is a Querydsl query type for BatchErrorLog
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBatchErrorLog extends EntityPathBase<BatchErrorLog> {

    private static final long serialVersionUID = -1521007L;

    public static final QBatchErrorLog batchErrorLog = new QBatchErrorLog("batchErrorLog");

    public final StringPath addin = createString("addin");

    public final StringPath cmp = createString("cmp");

    public final StringPath error = createString("error");

    public final StringPath form = createString("form");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath now = createString("now");

    public final StringPath old = createString("old");

    public final NumberPath<Long> oldId = createNumber("oldId", Long.class);

    public final NumberPath<Long> sum = createNumber("sum", Long.class);

    public QBatchErrorLog(String variable) {
        super(BatchErrorLog.class, forVariable(variable));
    }

    public QBatchErrorLog(Path<? extends BatchErrorLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBatchErrorLog(PathMetadata metadata) {
        super(BatchErrorLog.class, metadata);
    }

}

