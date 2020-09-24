# unibackend
行业后端
graphQL文档 https://www.graphql-java.com/documentation/
网页POST https://hanyeyinyong2.123nat.com:8673/graphql net::ERR_CERT_COMMON_NAME_INVALID 域名服务提供商有问题。

更新触发Gradle报错：右侧条gradle配置按钮-Use default gradle wrapper模式+gradle JVM版本12.1选;某些包下载遭遇失败,刷新重做，
看下载URL是国内的aliyun还是缺省https://plugins.gradle.org/m2/。
graphQL input递归,会导致 装载*.graphqls配置失败
graphQL用interface只能限定前端查询指定类型约束的可见字段，但无法限制前端修改查询结果的实际Type/通过它获得interface定义范围以外的字段。
　graphQL和java实体类两种系统独立但是又有联系的，有些部分必须各自独立定义，靠名字唯一性联系。实体继承性对graphQL不可见的。
接口return类型interface/union的，前端看到__typename是基本type/子类，__typename不会体现被继承的父类；但graphQL接口return类型是基本Type情形就不能体现其子类类名(无法...on)！
 接口返回类型会和实际java函数return类型对照；若一个类型影射2个的java类型初始化报错。return类型interface只要ObjectType内有字段即使interface没包含的也可被前端读取出;
 接口返回union类型的前端必须使用...on A/I{ }选择实体Type或者interface才能用{Fragment不可缺}=也能读出不在interface声明的隐藏Type{}字段!。
接口返回union的graphQL验证较轻(和java对应函数返回类型可不一致的)会延迟到实际执行JPA读取时刻。union组合若不包括继承子类的子Type对于java返回父interface的函数在读JPA时报错,union定义必须全包java的interface派生实体。
标准graphQL规范 https://github.com/graphql/graphql-spec/tree/master/spec
Controller在GraphQL内部提供{REST}，GraphQLXxResolver相当于Sevice层{Dao}，Repository这层{JPA/IMDG/SpringData/WebFlux}，再往下才是model/Entity/PoJo;堆叠4个层次。
遇到加Infinispan-starter-remote无法执行，IDEA调出RUN: Shorten command line @argFiles(java9+);

Null违例情况下查询缓存更新OK后再去点查询竟然看到更旧数据！查询不能替换掉实体缓存，graphql直接上实体缓存，须设更短更新时间或者杜绝非经过本系统去更新数据库。
人工修改数据库不会立刻反馈到本系统的缓存！人工删除实体可导致缓存查询爆错误。继承子类不能再做@org.hibernate.annotations.Cache()注解=抵消上级注解。查询缓存仅存储ID的,具体字段要从实体缓存读。
实体继承策略3种：SINGLE_TABLE策略(1 table,类个数不限|<500个+合计字段数<1000);JOINED策略(1+N table);TABLE_PER_CLASS策略{类个数<10+合计字段数<500个}。继承@MappedSuperclass抽象父类{不能有@Entity注解}。
Hibernate生成查询语句都是预定义=所有字段和jion表都是全写上的=实体继承导致SQL文本长度很大。JDBC性能考虑实体继承局限性:
JOINED实体继承策略局限:同根类个数<100+同根所有实体合计字段数<3000个,继承层次树最深20层。同一个根层次树继承策略只能定义一次，子类层次树底下就不能再变更继承策略。
mysql对表数量限制=OS可开文件句柄数65535个; Mysql按主键ID查询很快，存储N亿都没问题！InnoDB单表限制1017列。Row最大长缺省8KB可设置16KB最大。
mysql集群NDB支持20320个(表＋索引)；单张表限制512个列；NDB=分布式内存数据库,不能使用Innodb。最多支持145个节点；MySQL Group Replication组复制(基于Paxos协议), 分区发生脑裂,一组最多9个服务器。
MGR集群=mysql组复制必须存储在InnoDB,最多9个节点。MySQL Cluster或MySQL NDB Cluster是一个完全独立的产品=差异竞争对手，而这些服务器均不需要MySQL服务器实例。InnoDB Cluster几乎与MySQL Cluster完全无关！NDB=基于集群的存储。官网MySQL Cluster安装包是NDB。
MySQL Cluster使用NDB存储引擎，不需要在群集内的任何节点上安装MySQL Server软件; 对比的InnoDB Cluster却是为安装了MySQL Server软件的服务器提供了一种在它们之间复制数据的机制。MGR用InnoDB引擎简单。ndb cluster对于硬件环境要求比较高，不能跨IDC。适用于并发高小事务。
ES搜索引擎甚至比在本地MySQL通过主键的查询速度还快。es 久了会产生大量冗余数据，影响检索销量，可reindex，甚至从源一次，假如存储仅剩es 就麻烦了！
用Elasticsearch无法替代支持严格事务要求的关系数据库，不推荐完全用ES作为主要存储，每容量成本也比数据库高。ES改数据代价高，容量较低。
ES有索引更新延迟大,不能支持强实时性的要求。重要的数据别放里面！若遇到强关系关联数据的还是不行。　mysql-ES同步机制。
若数据库更新，debezium--MySqlConnector驱动发起的变更源消息，再转给spring-kafka消息集群来收集分发。

图数据库不支持严格的事务，而是最终一致性。spark sql适用OLAP/数据仓库,可跨数据源join，例如hdfs与mysql表join；spark.sql可利用ES的索引与搜索能力。
PySpark由python语言+Spark用的库,Spark作业job。
Elasticsearch-hadoop/spark是被动组件，允许Hadoop作业将其用作库source，并通过Elasticsearch for spark/Hadoop与Elasticsearch进行交互;保存到ES,随后从ES读取。
SparkSQL要扫描全库数据,所以慢。若对存储在hdfs海量数据字段建立索引，存在Elasticsearch中；根据Spark SQL字段条件，核心是通过Elasticsearch查询满足条件的Document ID，结果呢Spark直读记录就能加速海量OLAP查询。
Elasticsearch反而是用来帮助Spark做OLAP，而不是反过来的构想：用Spark来帮助Elasticsearch提速，完全相反。加载数据到ES中；
利用Spark读写Elasticsearch，那么从RDBMS读取后倒腾给ES,花费了3.6h = 超慢!,Spark用于统计应用场景=ETL/聚合/多源数据。
QueryDSL与SpringDataJPA同层，它也是基于各种ORM之上的一个通用查询框架，使用它可以写出“Java代码的sql”；
ES join性能差，nested query；has_child and has_parent within a single index；不像RDBMS表的join;
探究ES 明明存在，怎么搜索都搜不出来呢?　性能　查全率　查准率  https://cloud.tencent.com/developer/article/1380213
