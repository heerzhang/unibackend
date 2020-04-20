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

Redisson允许将Redis用作Hibernate缓存
<!-- Redisson Region Cache factory -->
<property name="hibernate.cache.region.factory_class" value="org.redisson.hibernate.RedissonRegionFactory" />
<!-- or 具有本地缓存​​支持-->
<property name="hibernate.cache.region.factory_class" value="org.redisson.hibernate.RedissonLocalCachedRegionFactory" />

redisson.yaml：这是单点的配置 https://blog.csdn.net/panzm_csdn/article/details/79700101
cd E:\del\Redis-x64-4.0.14.2　输入命令 redis-server.exe redis.windows.conf
正式有redis-5.0.5版本；Redis没有正式Windows。https://github.com/tporadowski/redis
单机Redis多个实例 初始化 https://www.jianshu.com/p/e90317668ae2
非收费版 Could not load　CachedRedisRegionFactory；　https://yq.aliyun.com/articles/554753?type=2
配置例子
https://github.com/redisson/redisson/blob/master/redisson-hibernate/redisson-hibernate-53/src/test/resources/redisson.yaml
http://www.voidcc.com/redisson/redisson-integration-with-hibernate

"redisson.yaml" org/hibernate/cfg/Configuration
        cfg.setProperty("hibernate.cache.redisson.item.eviction.max_entries", "100");
        cfg.setProperty("hibernate.cache.redisson.item.expiration.time_to_live", "1500");
        cfg.setProperty("hibernate.cache.redisson.item.expiration.max_idle_time", "1000");
    public static final String TTL_SUFFIX = ".expiration.time_to_live";
    public static final String MAX_IDLE_SUFFIX = ".expiration.max_idle_time";
Region名字实际出现在服务端dump.rdb内。过期时间在数据缓存时就定了不能更改，删除redis服务器dump.rdb才重置。
timeToLiveSeconds="120"

