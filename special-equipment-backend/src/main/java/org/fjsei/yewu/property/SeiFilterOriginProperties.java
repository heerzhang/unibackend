package org.fjsei.yewu.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.*;

@Data
@Configuration
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "sei.filter.origin")
public class SeiFilterOriginProperties {
    //private final List<String>   list = new ArrayList<>();
    private final Map<String, String>   map = new LinkedHashMap<>();
}


/* 配置 src\main\resources\citycode.properties 文件
citycode.list[0]=wuhan
      list[2]: https://localhost:3765
citycode.map.wuhan=4201
citycode.map.tianjin=1200
*/
