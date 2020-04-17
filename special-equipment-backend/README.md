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
cd E:\del\Redis　安装目录并输入命令redis-server.exe redis.windows.conf
正式有redis-5.0.5版本；Redis没有正式Windows。https://github.com/tporadowski/redis
非收费版 Could not load　CachedRedisRegionFactory；　https://yq.aliyun.com/articles/554753?type=2

