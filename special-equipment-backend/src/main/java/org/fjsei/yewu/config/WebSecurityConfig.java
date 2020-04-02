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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

//两地方@Configuration都做WebSecurityConfigurerAdapter报错@Order on WebSecurityConfigurers must be unique. Order of 100 was already used on WebSecurityConfig。
//加spring-boot-starter-security包注意必须处理，否则都报错；加了这个后 原来报错401变成403。

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


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
           auth.userDetailsService(jwtUserDetailsService)
            .passwordEncoder(passwordEncoderBean());
    }

    @Bean
    public PasswordEncoder passwordEncoderBean() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        //从中间拆分解开　middleRegistry；
        //<HttpSecurity>范型 必须加上，否则底下使用的第三个地方可能编译报错。
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry    middleRegistry;
        //we don't need CSRF because our token is invulnerable无懈可击; 由于使用的是JWT，我们这不需csrf
        //我们这graphql serlet在这前面已经会配设CorsFilter corsConfigurer()了，所以不要加.cors()咯。
        //http:/该加上.cors().and()

        middleRegistry =httpSecurity.csrf().disable()
              //  .cors().and()    //进入另外一种模式，缺省机制发挥作用。
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                // don't create session 无状态的，不需要服务器来维持会话数据。基于token所以不需session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests();

        //对preflight放行 OPTIONS请求实际上就是preflight(预检请求)。
        //上面.cors().and()配合graphql.servlet.corsEnabled: true组合下，本条分支配置就不起作用。？好多种的组合配置！！
        middleRegistry = middleRegistry.requestMatchers(CorsUtils::isPreFlightRequest).permitAll();

        // 所有 / 的所有请求 都放行;
        if(isPermitAnyURL) {
            //若PermitAnyURL=true那么：不登录REST就能随意访问任何接口； 但是graphQL没变化。
            //测试等场合，放开接入控制的。
            middleRegistry = middleRegistry.antMatchers("/**").permitAll();
        }else {
            //这里graphql是特权直通。权限控制在graphql配置里面再仔细控制！多个graphQL安全域接口配置：
            middleRegistry =middleRegistry.antMatchers("/graphql/**").permitAll()
                    .antMatchers("/public/**").permitAll()
                    .antMatchers("/third/**").permitAll()
                    .antMatchers("/subscriptions").permitAll()
                    .antMatchers("/subscriptions/**").permitAll()
                    .antMatchers("/forbidden").denyAll();
        }

        //        .antMatchers("/*").permitAll()
        //        .antMatchers("/u").denyAll()
        //        .antMatchers("/article/**").permitAll()
        //        .antMatchers("/v2/api-docs", "/swagger-resources/**","/webjars/**").permitAll() ;
        //permitAll().antMatchers("/manage/**").hasRole("ADMIN") // 需要相应的角色才能访问

        //除上面外的所有请求全部需要鉴权认证
        //TODO: 暂时去掉， subscription搞不定
        middleRegistry.anyRequest().authenticated();


        //控制要不要验证权限。
        //支持访问http://localhost:8083/voyager 这里并不需要添加路径啊？

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
                    HttpMethod.POST,
                    "/subscriptions/*","/auth"
            ).and()
                .ignoring()
                .antMatchers(
                    HttpMethod.GET,
                    "/teacher/**", "/graphiql", "/test/**", "/vendor/**","/subscriptions"
                ).and()
                   .ignoring()
                   .antMatchers(
                           HttpMethod.OPTIONS,
                           "/graphql","/auth"
                   );

         //若（isTestMode+isPermitAnyURL）任何人可随意访问任何接口，这时JWTcookies=null就被许可通行。
        }
        else {
            //这条路"/subscriptions"反而更惨，failed to access class MySubscriptionResolver，而另一路是陷入cors缺省值被拦截。
            web.ignoring()
                    .antMatchers(
                            HttpMethod.POST,
                            "/auth"
                    )
                    .and()
                    .ignoring()
                    .antMatchers(
                            HttpMethod.GET,
                            "/subscriptions"
                    );    //预留REST方式的登录， 报401错误，暂时还没实现它的rest方法
        }
     //若isTestMode但是isPermitAnyURL=false，没有登录JWTcookies=null的情形，还是无法访问以上资源的。必须!
    }

}


//graphiQL+新特性voyager 引入的 /vendor/* 路径；
