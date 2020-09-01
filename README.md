# unibackend
行业后端

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

