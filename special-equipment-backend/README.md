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