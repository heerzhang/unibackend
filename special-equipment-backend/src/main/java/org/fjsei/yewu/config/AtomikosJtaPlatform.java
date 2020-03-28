package org.fjsei.yewu.config;

import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

//这里JTA是跨越多个数据库的Transaction保障。
//JPA多个数据库实体相互间无法做关系模型关联的，hibernate底层自动生成SQL语句只能针对单一一个数据源来的，无法跨库join。
//graphQL的多个数据库实体模型不能重名的。

public class AtomikosJtaPlatform extends AbstractJtaPlatform {

	private static final long serialVersionUID = 1L;

	static TransactionManager transactionManager;
	static UserTransaction transaction;

	@Override
	protected TransactionManager locateTransactionManager() {
		return transactionManager;
	}

	@Override
	protected UserTransaction locateUserTransaction() {
		return transaction;
	}
}

//直接使用MySQL JDBC驱动连接跨实例查询服务，进行跨实例查询。 https://www.csdn.net/article/a/2019-01-14/15968748

