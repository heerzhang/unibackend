package org.fjsei.yewu.graphql;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//作废！

//名字有规则，prefix = "unibackend.tools"注意不能有大写字母的。

@Data
@ConfigurationProperties(prefix = "unibackend.tools")
public class MyGraphQLToolsProperties {

    private String schemaLocationPattern = "**/*.graphql";
    /** @deprecated Set graphql.tools.schema-parser-options.introspection-enabled instead */
    @Deprecated
    private boolean introspectionEnabled = true;
   //匹配application.yml的定义名称如下：unibackend . tools：
   // schema-location-pattern: "graphql/**/*.graphql"
   // introspection-enabled: true

    private boolean useDefaultObjectmapper = true;

}

