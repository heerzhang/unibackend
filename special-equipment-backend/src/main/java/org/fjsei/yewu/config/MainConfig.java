package org.fjsei.yewu.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.fjsei.yewu.AtomikosJtaPlatform;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

@Configuration
@ComponentScan
@EnableTransactionManagement
public class MainConfig {

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
		//hibernateJpaVendorAdapter.setShowSql(true);
		hibernateJpaVendorAdapter.setGenerateDdl(true);

		hibernateJpaVendorAdapter.setDatabase(Database.DEFAULT);
		//hibernateJpaVendorAdapter.setDatabase(Database.H2);
		return hibernateJpaVendorAdapter;
	}

	@Bean(name = "userTransaction")
	public UserTransaction userTransaction() throws Throwable {
		UserTransactionImp userTransactionImp = new UserTransactionImp();
		userTransactionImp.setTransactionTimeout(60);	  //60秒差不多，一个mutation或普通事务做完。
		//60秒 同样可能会是查询某些Query的内部某些阶段的限制，可能会超时。
		return userTransactionImp;
	}

	@Bean(name = "atomikosTransactionManager", initMethod = "init", destroyMethod = "close")
	public TransactionManager atomikosTransactionManager() throws Throwable {
		UserTransactionManager userTransactionManager = new UserTransactionManager();
		userTransactionManager.setForceShutdown(false);

		AtomikosJtaPlatform.transactionManager = userTransactionManager;

		return userTransactionManager;
	}


	@Bean(name = "transactionManager")
	@DependsOn({ "userTransaction", "atomikosTransactionManager" })
	public PlatformTransactionManager transactionManager() throws Throwable {
		UserTransaction userTransaction = userTransaction();

		AtomikosJtaPlatform.transaction = userTransaction;

		TransactionManager atomikosTransactionManager = atomikosTransactionManager();

		JtaTransactionManager jtaTransactionManager=new JtaTransactionManager(userTransaction, atomikosTransactionManager);

		//@Transactional事务几点注意:    https://blog.csdn.net/KinseyGeek/article/details/54931710
		//因为加Transactional(isolation = Isolation.SERIALIZABLE)	setAllowCustomIsolationLevels；DEFAULT （默认）使用数据库设置的隔离级别.
		//Oracle数据库支持READ COMMITTED 和 SERIALIZABLE这两种事务隔离级别。
		//Oracle，PostgreSQL，DB2，SQLServer数据库默认的事务隔离级别是Read Commited。　mysql默认的事务处理级别是REPEATABLE-READ；都不是最高级别的SERIALIZABLE。
		//设置整个会话的隔离级别  https://zhidao.baidu.com/question/1575430647131883500.html
		//幻读（Phantom Reads）事务T1读取在读取范围数据时，事务T2又插入一条数据，当事务T1再次数据这个范围数据时发现不一样了，出现了一些“幻影行”。
		//不可重复读（Non-repeatableReads）事务T1读取某一行数据后，事务T2对其做了修改，当事务T1再次读该数据时得到与前一次不同的值。
		//第二类（2）两次更新问题（Secondlost updates problem）: 两个事务都读取了数据，并同时更新，第一个事务更新结果将能被第二个事务所覆盖掉。
		//解决不可重复读的问题只需锁住满足条件的行，解决幻读需要锁表。
		//ISOLATION_DEFAULT：用底层数据库的默认隔离级别，数据库管理员设置什么就是什么;
		//隔离级别的设置只对当前链接/命令窗口有效;设置数据库的隔离级别一定要是在开启事务之前！
		//悲观锁：利用数据库本身的锁机制实现,根据具体业务情况综合使用事务隔离级别与合理的手工指定锁的方式比如降低锁的粒度等减少并发等待。
		//乐观锁利用程序处理并发。这种业务数据级别上的锁机制,加版本号.加时间戳。  https://blog.csdn.net/yuanyuanispeak/article/details/52756167

		jtaTransactionManager.setAllowCustomIsolationLevels(true);
		return  jtaTransactionManager;
	}

  	/*
	//@Primary
	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
		return new JpaTransactionManager(entityManagerFactoryFoo(builder).getObject());
	}
	*/
}
