# unibackend
行业后端
org/fjsei/yewu/config/MyGraphQLWebAutoConfiguration.java GraphqlFieldVisibility 


UrlBasedCorsConfigurationSource
SecurityFilterChain
UsernamePasswordAuthenticationToken
JwtAuthenticationProvider
http://www.imooc.com/article/264678
https://docs.spring.io/spring-security/site/docs/current/reference/html5/
https://github.com/spring-projects/spring-security/tree/5.3.0.RELEASE/samples/boot
https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&rsv_idx=1&tn=baidu&wd=spring%20boot%20addAllowedOrigin&rsv_t=1cecjDl5XcL0Liyjqt%2BCB5umhheqSl9D4BrXZ8o%2BRkNPWy2eGM2%2FTPo6cOE&rsv_dl=tb&rsv_enter=1&rsv_sug3=25&rsv_sug1=14&rsv_sug7=101&rsv_n=2&rsv_sug2=0&inputT=9542&rsv_sug4=11011

graphql asyncModeEnabled=false


@ConditionalOnProperty(value = "graphql.servlet.enabled", havingValue = "true", matchIfMissing = true)
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
