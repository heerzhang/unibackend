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

中文变量名 卡在graphQL的语法解析上。

