# unibackend
行业后端
https://www.bookstack.cn/read/Spring-Boot-Reference-Guide/README.md

UrlBasedCorsConfigurationSource
SecurityFilterChain
UsernamePasswordAuthenticationToken
graphql asyncModeEnabled=false

POST http://localhost:8673/third
Content-Type: application/json; charset=utf-8
Cache-Control: no-cache

{   "query": "{hello2(value:\"sdvvsd\") }" }

###
graphql后端测试
  query DEVICE_BY_ID($id: ID! ) {
    all:getDeviceSelf(id: $id) {
			id,oid,cod,isps{
				id
			},pos{
				id,name
			},ownerUnt{
				id,name
			},task{
				id,date,dep,status,isps{ id,dev{id} }
			}
		}
	}
参数
{
  "id": 1
}
关机
POST http://localhost:8673/actuator/shutdown
Content-Type: application/json; charset=utf-8

开启分布式缓存服务 cd E:\del\Redis-x64-4.0.14.2　输入命令 redis-server.exe redis.windows.conf
hibernate的二级缓存  https://blog.csdn.net/panzm_csdn/article/details/79700101
正式有redis-5.0.5版本；Redis没有正式Windows。 https://github.com/tporadowski/redis
单机Redis多个实例 初始化  https://www.jianshu.com/p/e90317668ae2
非收费版 Could not load　CachedRedisRegionFactory；　https://yq.aliyun.com/articles/554753?type=2
配置例子  Redission说明书：  https://github.com/redisson/redisson/tree/master/redisson-hibernate
小院，Redission多个模式：  http://www.voidcc.com/redisson/redisson-integration-with-hibernate
开启http2后Header变成小写字母且keep-alive没有了，wss/https类比端口若不同无应答。
有域名可网上申请SSL证书。nat123免费二级域名http://www.nat123.com/ 用户 herzhang;  http://hanyeyinyong2.123nat.com:3765/
mySQL启动数据库：net start mysql   登录：mysql -u root -p  
https://hanyeyinyong2.123nat.com:3765       前端升级了开发模式wss可用。

中文变量名 卡在graphQL的语法解析上。  *.graphqls模型内直接定义union Device = EQP|Elevator|;实体Elevator extends EQP;
　用eQPRepository就能读写Elevator数据库，只需用Iterable<EQP> findAll()就能满足graphQL模型findAll(): [Device],前端... on Elevator区分解析。
graphQL接口返回列表[union]时的union成员必须有同一个基类实体类，否则无法匹配Java函数/类。
需要运行时绑定wiring绑定到Java方法，将schema文件和wiring结合创建可执行schema；PropertyDataFetcher从Map和Java Bean获取。当字段名称与Map中key或Bean对象属性相同，无需显式指定DataFetcher。
 graphQL标准String Boolean Int Float ID；扩展Long Short Byte Float BigDecimal BigInteger；支持type递归A{c:A}；extend type A implements X {}和extend type A{}会全部合并同名字段。
graphQL模型接口return和java接口函数返回类型并不严格要求可以Cast()直接转换的！，只要能够获得相应名称属性就能运行。
GraphQL："枚举GraphQLEnumType是输入输出都能做"；    #外模型看到的字段和POJO可不同；  不能用type定义的对象类型来做输入参数=报错
#复合众多参数Input types may be lists of another input type, or a non‐null variant of any other input type像JS对象样；
input关键字来到定义输入类型，不直接用Object是为了避开循环引用、接口或联合等一些不可控的麻烦，特别是input类型不能像Object那样带参数的。
升级后不能用WhereTree来做前端查询了！input类型不直接使用Object Type呢？Object字段可能存在循环引用，或字段引用不能作为查询输入的接口和联合类型。
graphQL input递归无法再使用！导致ModelFilters这层类sql看来要退出前端动态解释型领域，只剩下给不想写HQL代码的后端程序静态型用。
这O.p()/O.getP()/O.p三者是统一的。graphql接口不支持重载的虽然java同名函数参数类型不同可支持。
不用interface直接用union和实体类继承可以做graphql统一接口返回类型union，这方法前端... on EQP需要写重复字段; ... on X{ }这若X纯粹模型文件定义的非java类也可以。
若graphQL模型定义A implements I{}的，对照地java实体类同样需要A implements I{}；　不涉及接口返回类型的implements{前端用到的}可以不用在java中定义。
graphQL接口方法或Object Type模型至少有一处有引用到的；才可以进入SchemaClassScanner.dictionary法眼，否则找不到:Type S implements I{}模型。
　　Union联合类型成员须是具体对象类型，不能用接口或其他联合型来 | ;   内置模型PageInfo {endCursor,}关联Relay/Edges/connection；
*.graphqls模型内定义implements和java实体类定义implements两个都是必须的/是独立验证的；B implements I{}。 能支持没有java继承关系类合并查询做graphQL接口返回结果类型/都做implements I{}方式。
type N{}和interface N{}可以同名N。interface B{b..};C implements B{c..}不需要java类配合的,但是这里c..字段必须包含全部的B字段b..一个都不能少！！interface N{}不需要java中定义N的/无关联java的interface implements。
graphQL Union=|必须是Object Type,不检查查询结果类;但是只能用...on{}取字段。 　enum D { EAST }是字符常量。
GraphQLObjectType GraphQLInterfaceType 之间不可以相同名字的类型定义。union与interface之间也不可同名字，否则后面的覆盖前面的。

type EQP{
    id:ID!
    cod: String!
    oid: String
    type: String!
    sort: String!
    vart: String!
    ownerUnt: Unit
    pos: Address
    maintUnt: Unit
    instDate: String
    factoryNo: String
    task: [Task]
    isps: [ISP]
    #外模型看到的字段和POJO可不同
    meDoIsp: [ISP]
    valid: Boolean
}
interface Equipment {
    id:ID!
    cod: String!
    oid: String
    type: String!
    sort: String!
    vart: String!
    ownerUnt: Unit
    pos: Address
    maintUnt: Unit
    instDate: String
    factoryNo: String
    task: [Task]
    isps: [ISP]
    valid: Boolean
}

type Elevator {
    id:ID!
    cod: String!
    oid: String
    type: String!
    sort: String!
    vart: String!
    ownerUnt: Unit
    pos: Address
    maintUnt: Unit
    instDate: String
    factoryNo: String
    task: [Task]
    isps: [ISP]
    valid: Boolean
    liftHeight: String
}

infinispan概念部署模式Cache Manager有俩个形式：Embedded，+而C\S,Remote；
cache六个模式：2=Local单机，5=Distributed模式集群多份冗余，3=Invalidation改删除，4=Replicated全拷{<10节点}。 Local Cache是单个服务器版。 乐观锁抛出异常。
4种Clustered集群模式(3,4,5)都要JGroups预配传输协议。5Distributed模式也会有local cache吗？-> L1 is enabled暂时节点内(非默认开)；不建议异步通信;异步模式时read-committed isolation实际用repeatable-read实现；
集群才需配<transport stack="udp" cluster="myName"/> 集群JGroups定义stacks{UDP/TCP};。
RemoteCacheManager配置要经过HotRod{需配IP+Port}；外Storage不支持TX一致性？file-store可以，不能用NFS做Store。
C\S模式客户端连接到Infinispan服务器只需指定任意服务器的IP地址和端口号即可。服务器会将拓扑信息发给客户端，变化新的拓扑信息也会同步到客户端，
hash分布感知将节点选择落在客户端完成; <jgroups>定义AUTH/ENCRYPT{节点间}。如何验证HotRod客户端身份密码？
服务器urn:infinispan:server底下定义<endpoints> security realm认证用户；　文档https://infinispan.org/docs/stable/titles/server/server.html#securing_access
infinispan缺省缓存100s。 CLI命令手册https://infinispan.org/docs/stable/titles/cli/cli.html
hibernate L2C 对于Repository的函数须各自声明Cache才能缓存。
无法用infinispan-spring-boot-starter-remote只能-embedded部署模式，端口不是独立服务器11222而是Embedded方式自己集群搞的7800{default-jgroups-tcp.xml这里配}。
缺省是=Invalidation(synchronous)模式{节省集群流量}；缺省逐出唤醒间隔为5秒，最大条目数为10000，到期前的最大空闲时间为100秒。需要集群服务器的挂钟同步才能。
支持组合：non-transactional；distributed/replicated；不能设置Eviction。　hibernate.cache.use_minimal_puts 应该开启？
Spring的@Cacheable做法是用infinispan-spring5-embedded或infinispan-spring5-remote不经过hibernate；若-remote情况设置hotrod-client.properties。做Spring Session场景@EnableInfinispanRemoteHttpSession。
Hibernate OGM缺点不能做复杂关系查询=NoSQL。 图数据库JanusGraph, Cassandra+Spark；SparkSQL/并行；Cassandra/HBase; Elasticsearch；Gremlin Groovy；若存储引擎是BerkeleyDB那支持ACID，但Cassandra或HBase不支持ACID。
若用BerkeleyDB做的存储后端：适合在最多达1亿个顶点的中小型图形，被限制在单个计算机运行，​​因此并发请求数也受到限制；为了不耗尽内存，建议图遍历时禁用事务！Cassandra集群400台计算机300TB数据。Elasticsearch集群；
分布式系统强一致性的需求可实现＝Quorum协议 W+R>N/记录的最新版；Quorum类比Paxos算法；这里一致性实际理解是分区P容忍性。

Elasticsearch去规范化，join不能跨索引，ES子文档和父文档都必须位于相同的索引和相同的分片中。 child/parent;不建议用多级关系。 每个关系级都会在增内存计算开销。应该对数据进行非规范化!
每个索引仅允许一个join字段映射。可以有多个子级，但只能有一个父级parent=多对多被掐掉。
死机导致损坏而报错C:\Users/AppData\Local\Temp/servlet-sessions\spring-boot.session删除重启。
@ManyToOne字段会提前读取，指出@ManyToOne(fetch= FetchType.LAZY)懒读。
@DiscriminatorColumn只能标注在顶层的类中，而不能标注在子类中+只在继承策略为“SINGLE_TABLE”和“JOINED”时使用。。
MySQL普通表最多撑死2千万条Row。
索引字段类型，影响性能，int最的，字符类型略差;复合索引a,b,c；在a,a b,a b c情况会用到；
函数表达式不使用索引; like ‘a%’可以用上索引。
全文索引like ‘%A%’派上用场;fn(A)=‘V’不能走索引应改成A=fn(‘V’);
graphql定义的type若遇interface union的必须最少在接口函数中引用一次。
IK分词器安装　插件　https://blog.csdn.net/lgb190730/article/details/107882929?utm_medium=distribute.pc_relevant.none-task-blog-title-8&spm=1001.2101.3001.4242
分词插件，版本必须和ES配套;　https://www.cnblogs.com/gwyy/p/12205257.html

单位列表查询3类接口：搜索，精确搜索，查找。搜索=ES中用match_phrase，精确搜索=ES中用wildcard，查找=数据库中Like查询。性能是高到底的。
十九种Elasticsearch搜索方式,数据量很大用MySQL会造成慢查询=想改用Elasticsearch;  https://blog.csdn.net/valada/article/details/105607918
graphql-java-tools 6.1版本开始不能用WhereTree来做前端查询？bug?，input类型递归不能搞了；暂弃部分(高权限接口才能用的功能)如下：
    findAllEQPsFilter_delete(where: WhereTree, offset:Int, first:Int, orderBy:String, asc:Boolean): [EQP]!
ModelFilters这层类sql接口预备给高权限场景使用WhereTree(参数可能不受控制)，普通接口走参数定做模式/多写代码。
union UnitEs= CompanyEs | PersonEs 报错，PersonEs必须在graphQL模型中被用到。
