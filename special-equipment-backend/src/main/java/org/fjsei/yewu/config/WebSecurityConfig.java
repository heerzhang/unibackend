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
        // we don't need CSRF because our token is invulnerable无懈可击
        middleRegistry =httpSecurity.csrf().disable()
                .cors().and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                // don't create session 无状态的，不需要服务器来维持会话数据。
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests();

        if(isPermitAnyURL) {
            //若PermitAnyURL=true那么：不登录REST就能随意访问任何接口； 但是graphQL没变化。
            //测试等场合，放开接入控制的。
            middleRegistry = middleRegistry.antMatchers("/**").permitAll();
        }else {
            //这里graphql是特权直通。权限控制在graphql配置里面再仔细控制！多个graphQL安全域接口配置：
            middleRegistry =middleRegistry.antMatchers("/graphql/**").permitAll()
                    .antMatchers("/public/**").permitAll()
                    .antMatchers("/third/**").permitAll()
                    .antMatchers("/subscriptions/**").permitAll()
                    .antMatchers("/forbidden").denyAll();
        }

        middleRegistry.anyRequest().authenticated();

        //控制要不要验证权限。
        //支持访问http://localhost:8083/voyager 这里并不需要添加路径啊？

        //httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterAfter(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        //看boot自带登录页面.formLogin().loginPage("/login").permitAll();

        // disable page caching
        httpSecurity
                .headers()
                .frameOptions().sameOrigin()  // required to set for H2 else H2 Console will be blank.
                .cacheControl();
    }

    //订阅消息`ws://localhost:9000/subscriptions`也走这里的。 上面HttpSecurity "/subscriptions/**"不加走不通。
    //静态的文件内容-可以允许那些非授权=没token访问的内容；
    @Override
    public void configure(WebSecurity web) throws Exception {
        //AuthenticationTokenFilter 不过滤的内容-文件; Spring Security要忽略的部分;
        //非生产的和系统调试用的。 正式生产环境配置=false;
        if(isTestMode) {
           //web.ignoring().antMatchers()不能用多次？。
           web.ignoring().antMatchers(
                    HttpMethod.POST,
                    "/graphiql/*","/subscriptions/*","/auth"
            ).and()
                .ignoring()
                .antMatchers(
                    HttpMethod.GET,
                    "/teacher/*", "/graphiql", "/test/*", "/vendor/*","/subscriptions/*"
                );
         //若（isTestMode+isPermitAnyURL）任何人可随意访问任何接口，这时JWTcookies=null就被许可通行。
        }
        else {
            web.ignoring()
                    .antMatchers(
                            HttpMethod.POST,
                            "/auth"
                    );    //预留REST方式的登录， 报401错误，暂时还没实现它的rest方法
        }
     //若isTestMode但是isPermitAnyURL=false，没有登录JWTcookies=null的情形，还是无法访问以上资源的。必须!
    }

}


//graphiQL+新特性voyager 引入的 /vendor/* 路径；
