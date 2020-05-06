package org.fjsei.yewu.config;

import org.fjsei.yewu.security.JwtAuthenticationEntryPoint;
import org.fjsei.yewu.security.JwtAuthorizationTokenFilter;
import org.fjsei.yewu.service.security.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

//两地方@Configuration都做WebSecurityConfigurerAdapter报错@Order on WebSecurityConfigurers must be unique. Order of 100 was already used on WebSecurityConfig。
//加spring-boot-starter-security包注意必须处理，否则都报错；加了这个后 原来报错401变成403。
//对比者shiro文檔Spring Security体系及其核心    https://www.kancloud.cn/liquanqiang/spring-security/750414


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    // Custom JWT based security filter
    @Autowired
    JwtAuthorizationTokenFilter authenticationTokenFilter;


  //  @Value("${jwt.route.authentication.path}")
  //  private String authenticationPath;
    @Value("${sei.testMode:false}")
    private Boolean isTestMode;
    @Value("${sei.control.permitAnyURL:false}")
    private Boolean isPermitAnyURL;
    @Value("${sei.BCrypt.strength:11}")
    private int bCryptStrength;

    //这里AuthenticationManagerBuilder还属于较为上层的介入通道或模式机制 JDBC，inMemory，DAO提供用户信息库的。
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
         // auth.userDetailsService(jwtUserDetailsService)
        auth.authenticationProvider(authenticationProvider());
           // .passwordEncoder(passwordEncoderBean());
    }

    //没用到endpoint接口/login请求/logout"记住我"指定数据源Cookie页面跳转，自然走的不是springSecurity自带的很多功能封装点块，我们没走AuthenticationProvider这条路。
    @Bean
    public AuthenticationProvider authenticationProvider(){
        //DaoAuthenticationProvider用于响应UsernamePasswordAuthenticationToken身份验证请求
        //实际我们没有用DaoAuthenticationProvider，但我们提供了和DaoAuthenticationProvider同样的功能，属于同一个层次或点位嫁接位置的。
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(jwtUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoderBean());
        return authenticationProvider;
    }

/*
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }
*/

    @Bean
    public PasswordEncoder passwordEncoderBean() {
        //随机盐參數也在密文里面,只能支持72个字符密码长度！速度很慢，故意的？1秒验证; https://www.kancloud.cn/liquanqiang/spring-security/757177
        //参数修改后，数据库遗留Hash密码不需要重新生成，旧Hash照样能够验证匹配的！前端js里面有同样功能bcrypt库，也是从DB数据库读取hash来核对验证的。
        PasswordEncoder encoder=new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2Y, bCryptStrength);
        //慢哈希是指执行这个哈希函数非常慢! 实际电脑性能调整。Salt防彩虹表攻击，自适应功能，增加迭代计数以使其执行更慢，在增加计算能力仍然能保持抵抗暴力攻击。
        //参数strength调节对性能服务器CPU影响：strength取值20耗时242s,取15耗时8s,取14耗时4s,取13耗时2s,而strength缺省=10的;看实际生产环境承受能力确定strength。
        //strength亦称为LOG ROUND参数是做加密时重复处理的轮数，且复杂度是指数级递增，默认10的是2的10次方; 实际生成hash长度大约23字节的空间=184位。
        return encoder;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //WebSecurity是上层较粗的会忽略那些的。反而HttpSecurity下层细节属于更加精细更消耗计算量的进一步深度控制，WebSecurity话语权优先。
    //假如WebSecurity.ignoring().antMatchers("/subscriptions"，那么HttpSecurity.antMatchers("/subscriptions").permitAll()就没有必要添加。
    //订阅消息`ws://localhost:9000/subscriptions`也走这里的。 上面HttpSecurity "/subscriptions/**"不加走不通。
    //静态的文件内容-可以允许那些非授权=没token访问的内容；
    @Override
    public void configure(WebSecurity web) throws Exception {
        //AuthenticationTokenFilter 不过滤的内容-文件; Spring Security要忽略的部分;
        //非生产的和系统调试用的。 正式生产环境配置=false;
        //其实isTestMode和PermitAnyURL独立影响。！注意 vendor/* vendor/** vendor/ vendor 区别很大。
        if(isTestMode) {
            //开启graphiql测试工具
            web.ignoring().antMatchers(
                    HttpMethod.POST, "/auth"
            ).and()
                    .ignoring()
                    .antMatchers(
                            HttpMethod.GET,
                            "/teacher/**", "/graphiql", "/test/**", "/vendor/**"
                    ).and()
                    .ignoring()
                    .antMatchers(
                            HttpMethod.OPTIONS,
                            "/graphql","/auth"
                    );

            //若ignoring(HttpMethod.GET, "/subscriptions")将会导致subscriptions场景auth就算有token却也是null的。
            //若（isTestMode+isPermitAnyURL）任何人可随意访问任何接口，这时JWTcookies=null就被许可通行。
        }
        else {
            //这条路"/subscriptions"反而更惨，failed to access class MySubscriptionResolver，而另一路是陷入cors缺省值被拦截。
            web.ignoring()
                    .antMatchers(
                            HttpMethod.POST,
                            "/auth"
                    );
                    /*.and()
                    .ignoring()
                    .antMatchers(
                            HttpMethod.GET,
                            "/subscriptions"
                    );*/

            //预留REST方式的登录， 报401错误，暂时还没实现它的rest方法
        }
        //若isTestMode但是isPermitAnyURL=false，没有登录JWTcookies=null的情形，还是无法访问以上资源的。必须!
    }

    //关系颠倒很别扭：WebSecurity是上层大门粗略放行，反而HttpSecurity下层明细控制，上层已经放行的，下层就不会再检查了。
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        //从中间拆分解开　middleRegistry；
        //<HttpSecurity>范型 必须加上，否则底下使用的第三个地方可能编译报错。
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry    middleRegistry;
        //前端在每次请求时将JWT放入HTTP Header中的Authorization位。(解决XSS和XSRF问题)
        //token is invulnerable无懈可击; 由于使用的是JWT，我们这不需csrf
        //我们这graphql serlet在这前面已经会配设CorsFilter corsConfigurer()了，所以不要加.cors()咯。
        //http:/该加上.cors().and()
        //在使用graphQL或REST 端点时，您不必担心使用CSRF保护,对服务的请求应该是无状态的！
        //graphQL用无状态的， 但似乎也可以做成有状态模式。

        middleRegistry =httpSecurity.csrf().disable()
                //.cors().and()    //加了就进入另外一种模式，缺省机制发挥作用。
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                // don't create session 无状态的，不需要服务器来维持会话数据。基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests();

        //对preflight放行 OPTIONS请求实际上就是preflight(预检请求)。
        //上面.cors().and()配合graphql.servlet.corsEnabled: true组合下，本条分支配置就不起作用。？好多种的组合配置！！
        //预检PreFlightRequest就是　OPTIONS的。
        middleRegistry = middleRegistry.requestMatchers(CorsUtils::isPreFlightRequest).permitAll();

        // 所有 / 的所有请求 都放行;
        if(isPermitAnyURL) {
            //若PermitAnyURL=true那么：不登录REST就能随意访问任何接口； 但是graphQL没变化。
            //测试等场合，放开接入控制的。
            middleRegistry = middleRegistry.antMatchers("/**").permitAll();
        }else {
            //这里graphql是特权直通。权限控制在graphql配置里面再仔细控制！多个graphQL安全域接口配置："/graphql/**"
            middleRegistry =middleRegistry.antMatchers("/graphql").permitAll()
                    .antMatchers("/public").permitAll()
                    .antMatchers("/third").permitAll()
                    .antMatchers("/subscriptions").permitAll()
                    .antMatchers("/forbidden").denyAll();
        }

        //spring security判断区别:字符串加了ROLE_就是一个角色Role，如果没有加ROLE_的字符串就代表一个权限Authority!
        //        .antMatchers("/*").permitAll()
        //        .antMatchers("/voyager").denyAll()
        // .antMatchers("/admin").hasAuthority("admin")   // 需拥有 admin 这个权限
        //  .antMatchers("/ADMIN").hasRole("ADMIN")     // 需拥有 ADMIN 这个身份
        //        .antMatchers("/article/**").permitAll()
        //        .antMatchers("/v2/api-docs", "/swagger-resources/**","/webjars/**").permitAll() ;
        //permitAll().antMatchers("/manage/**").hasRole("ADMIN") // 需要相应的角色才能访问

        //除上面外的所有请求全部需要鉴权认证
        middleRegistry.anyRequest().authenticated();

        //没用middleRegistry.and().formLogin()所以很多springSecurity包模块就没用没效，实际上我们做了同样的功能替换掉。
        //控制要不要验证权限。

        //httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterAfter(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        //看boot自带登录页面.formLogin().loginPage("/login").permitAll();

        // 禁用缓存
        // httpSecurity.headers().cacheControl();
        // disable page caching
        httpSecurity
                .headers()
                .frameOptions().sameOrigin()    //required to set for H2 else H2 Console will be blank.
                .cacheControl();

        //添加未授权处理
       //   httpSecurity.exceptionHandling().authenticationEntryPoint(getAuthenticationEntryPoint());
        //权限不足处理
       //  httpSecurity.exceptionHandling().accessDeniedHandler(getAccessDeniedHandler());

    }

}



//Authentication：登录，验证用户是不是拥有相应的身份； Authorization：授权，即权限验证，验证某个用户是否拥有某个角色。  @PreAuthorize("hasAnyRole('admin','user')")
//voyager支持包 引入的 "/vendor/**"路径；voyager实用性不强，摆设罢了，删除掉不再支持。
