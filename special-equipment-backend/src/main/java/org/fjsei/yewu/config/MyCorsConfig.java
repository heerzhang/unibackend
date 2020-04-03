package org.fjsei.yewu.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

//作废本文件！
//注释掉@Configuration就不会执行这个配置文件咯。

@Deprecated(forRemoval=true)
//@Configuration
public class MyCorsConfig {
    //@Bean 名字同名，名字不能乱用 corsFilter()就报错了
    //实际比　ＪＷＴｘｘｘ验证提前了！
    //两个地方都做CORS;   若这里开启配置会进入缺省DefaultCorsProcessor，可能无法进入下一个JwtAuthorizationTokenFilter，导致http跨域请求受阻。
    @Bean
    public FilterRegistrationBean  myAddcorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://localhost:3765");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }
    //为何ws//subscription程序会进入 org/springframework/web/cors/DefaultCorsProcessor.java:84，两种协议CORS处理流程还不一样。http/和ws:/的。
}

