package org.fjsei.yewu.security;

import org.fjsei.yewu.property.SeiFilterOriginProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

//Spring Security 登录密码验证过程 UsernamePasswordAuthenticationFilter 实际功能和这个类似的。而这里我们自己做了。
//UsernamePasswordAuthenticationFilter在调用http.formLogin()时用，登录请求"/login"，登录loginProcessingUrl委托认证管理器authenticationManager来验证登录。

//过滤器实际也是注入的。
@Component
public class JwtAuthorizationTokenFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //这3个参数Spring直接注入默认初始化？？所以实际上这３个变量也算被自动构造注入的。
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;        //所有请求公共的。
    private final String tokenHeader;

    //注入application.yml配置
    @Value("${sei.filter.optionsAge:600}")
    private final String optionsCacheTime;
    @Value("${sei.testMode:false}")
    private boolean  isTestMode;
    @Value("${sei.server.URI:}")
    private final String  serverURI;
    //允许设置多个CORS 域名;
    @Autowired
    private SeiFilterOriginProperties seiFilterOriginProperties;

    //单次请求过滤器；这个是被Spring直接注入的。参数默认初始化了。
    public JwtAuthorizationTokenFilter(UserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil, @Value("${jwt.header}") String tokenHeader) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.tokenHeader = tokenHeader;
        optionsCacheTime="";
        serverURI="";
    }

    //JSON Web Token（JWT）是一个开放式标准（RFC 7519）
    //一般放在HTTP的headers 参数里面的authorization里面，值的前面加Bearer关键字和空格。
    //不同应用系统可能要求用户和用户权限并不一致，? 考虑用户权限模型部分的恰当的独立性。

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String originHeads = request.getHeader("Origin");
        if(seiFilterOriginProperties.getMap().containsKey(originHeads)){
            //设置允许跨域的配置允许进行跨域的主机ip（正式上线时可以动态配置具体允许的域名和IP）
            //不设置就 被此处拦截spring-web/org/springframework/web/filter/CorsFilter.java:90掉。
            response.setHeader("Access-Control-Allow-Origin", originHeads);
        }
        if (request.getMethod().equals("OPTIONS")){     //浏览器自主决定的请求，不是用户决定。
            //logger.debug("浏览器的预请求的处理..");
            //这一步只有http初始化可能发生也是时间过期了才有。　而ws://都没有走到这里。
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET,HEAD,PUT,PATCH,POST,DELETE");
            response.setHeader("Access-Control-Max-Age", optionsCacheTime);
            response.setHeader("Access-Control-Allow-Headers", "Origin,X-Requested-With,Content-Type,Accept,Authorization,token");
            response.setHeader("Connection", "keep-alive");
            response.setHeader("Vary", "Origin, Access-Control-Request-Headers");
            return;
        }else {
            String servletPath = request.getServletPath();
            String method = request.getMethod();
            if ("GET".equals(method) && "/forbidden".equals(servletPath)) {
                //response.setContentType("text/html;charset=utf-8");  getMethod()
                if (originHeads.equals(serverURI)) {
                    //本机端口影射到外网后的，＋从外网访问＋，防火墙主动发起的，和浏览器毫无关系的。.心跳吗？
                    return;
                } else {
                    response.sendError(404, "资源不存在");
                    return;     //禁止使用的URL
                }
            }

            if (isTestMode) {
                if (servletPath.startsWith("/test/")
                        || servletPath.startsWith("/favicon.ico")) {
                    chain.doFilter(request, response);
                    return;      //没必要授权的，只在开发测试阶段使用的URL
                }
            }

            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Connection", "keep-alive");
            //response.setHeader("Vary", "Origin");     ??重复了

            final String requestHeader = request.getHeader(this.tokenHeader);       //取名字是Authorization的参数；
            String authToken = null;
            //注销时：OPTIONS过后requestHeader="";    ?Bearer 和token= 不是同一个参数。
            //?标准没Bearer ;
            //新版graphiQL的客户端都会给服务器Authorization: Bearer ? + cockie=俩个token都带来
            if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
                //浏览器上人工添加Authorization token来做验证权限的场景走这个分支
                authToken = requestHeader.substring(7);
            } else {    //没有Authorization: Bearer开头;
                //graphQL的playground工具(相当集成在后端上的endpoint)无法带上Cookie必须走Bearer开头。
                //調試這位置，只有運行request.getCookies()后，request才有cookie；这块是实时交互式的读取浏览器的数据？不是浏览器发送请求包已经就带的数据。
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    Cookie tokenCook = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("token")).findFirst().orElse(null);
                    if (tokenCook != null)
                        authToken = tokenCook.getValue();
                    //手机端支持？
                }
            }

            //if("/subscriptions".equals(servletPath)) { }
            //尝试建立用户身份权限信息注册设置到spring security层。但是Session .STATELESS每一次请求包都需要再次设置身份信息。
            jwtTokenUtil.continuedTokenLifeAuthentication(this.userDetailsService, request, response, authToken);
            //還未登錄的用戶請求Authentication .getAuthentication()這個位置還是null;就是graphql驗證和做登录處理需要允許直接通過。
            //真正控制还要到上层协议里头去控制，每一个graphQL请求都要验证Auth,可见性MyGraphQLWebAutoConfiguration。
            //没登录用户走直接REST模式能来到这，但是spring security底层会钩住它401报错。

            if (false) {
                //调试插桩代码： 只能临时开启，用来定位问题的，直接获取请求包原文数据。
                StringBuffer data = new StringBuffer();
                String line = null;
                BufferedReader reader = null;
                try {
                    reader = request.getReader();
                    while (null != (line = reader.readLine()))
                        data.append(line);
                } catch (IOException e) {
                } finally {   }
                //随后运行就报错graphql.GraphQLException: No valid query found in request
            }

            chain.doFilter(request, response);
            //就算Url没有要求授权验证它也可能会到这里。
        }
    }
}



/*
浏览器clear cookie可能清理不掉前后端分离架构下的后端Domain的所有token,对Subscription清理后还能携带多个token，后端cookie共用的与前端域名端口是啥没相关。
Cookie 的一个独特之处在于，浏览器会自动为每个请求附加到特定域和子域的 Cookie 到 HTTP 请求的头部。
这就意味着，如果我们将 JWT 存储到了 Cookie 中，假设登录页面和应用共享一个根域，那么在客户端上，我们不需要任何其他的的逻辑，就可以让 Cookie 随每一个请求发送到应用服务器。
但是另一方面，它引入了一个新的问题 —— XSRF。
主流框架都带有防御措施，可以很容易地对抗 XSRF（跨站请求伪造），CSRF 因为它是一个众所周知的漏洞。
就像是发生过很多次一样，使用 Cookie 意味着利用 HTTP Only 可以很好的防御脚本注入；  https://www.imooc.com/article/40816
GraphQL可以理解为基于RESTful的一种封装，目的在于构建使Client更加易用的服务，可以说GraphQL是更好的RESTful设计。
HTTP无状态协议和Connection:Keep-Alive容易犯的误区: https://www.cnblogs.com/watermarks/articles/3766310.html
理解cookie，session，token; CORS(跨域资源共享)  https://www.liangzl.com/get-article-detail-16019.html
*/
