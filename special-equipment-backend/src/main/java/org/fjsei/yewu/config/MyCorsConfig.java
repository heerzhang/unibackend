package org.fjsei.yewu.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class MyCorsConfig {
/* @Bean 名字同名，名字不能乱用 corsFilter()就报错了
org.springframework.beans.factory.BeanNotOfRequiredTypeException: Bean named 'corsFilter' is expected to be of type 'org.springframework.web.filter.CorsFilter' but was actually of type 'org.springframework.boot.web.servlet.FilterRegistrationBean'
*/

    //实际比　ＪＷＴｘｘｘ验证提前了！
    //todo: ? 两个地方都作了ＣＯＲＳ？？
    //subscription正常了 http3765原前端却被cors拦截。 相反机制作用？两者对着干。

    @Bean
    public FilterRegistrationBean  myAddcorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://192.168.1.105:3000");
        config.addAllowedOrigin("http://localhost:3765");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);        return bean;

        //不起做了！！
    }

    //为何ws//程序会进入 org/springframework/web/cors/DefaultCorsProcessor.java:84，两种协议CORS处理流程还不一样。http/和ws:/的。
}

