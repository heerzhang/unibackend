package org.fjsei.yewu.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Configuration
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "sei.filter.origin")
public class SeiFilterOriginProperties {

    private final List<String>   list = new ArrayList<>();

    private final Map<String, String>   map = new HashMap<>();

}


/* 配置 src\main\resources\citycode.properties 文件
citycode.list[0]=wuhan
citycode.list[1]=http://localhost:3000
citycode.list[2]=tianjin
citycode.map.wuhan=4201
citycode.map.tianjin=1200
*/
