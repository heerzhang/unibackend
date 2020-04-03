package org.fjsei.yewu.graphql;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLFieldsContainer;
import graphql.schema.visibility.DefaultGraphqlFieldVisibility;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

//从*.graphqls文件注入角色权限控制机制，给外模型字段添加内省安全能力。
//安全过滤自定义 directive @authr，因为角色权限而屏蔽给前端看的外模型的字段，返回null,考虑缺省角色以及可能特殊情况的字段。
//这个是针对接口函数的一次性过滤字段，而不能针对单条数据记录来做细分上的过滤,不会因每一条数据记录都运行到这里。


public class MyGraphqlFieldVisibility extends DefaultGraphqlFieldVisibility {
    private String defaultRole;
    public MyGraphqlFieldVisibility(String defaultRole){
        this.defaultRole=defaultRole;
    }

    //一次查询就可能来多次，中间运行AuthrDirective.onField的登记好大的钩子，+1次，这里过滤优先。
    //这里安全控制机制只能当作基础门槛，控制力度比较弱的。要强的就要SDL注解，Java注解，代码层次个别判定。
    @Override
    public GraphQLFieldDefinition getFieldDefinition(GraphQLFieldsContainer fieldsContainer, String fieldName) {
        GraphQLFieldDefinition field =fieldsContainer.getFieldDefinition(fieldName);
        //若有被@authr注解的直接通过｛留给AuthrDirective处理｝，defaultRole是没有@authr注解的任何字段方法的权限要求。
        if(defaultRole==null)       //defaultRole=""代表随意都能访问缺省没有"@authr"注解的字段或方法。
            return field;
        //执行到这时，还处于parseAndValidate堆栈，还没有获取数据呢，无法区分id。　多次调用最后ExecutionStrategy回已经取了子对象authorities数据没有User数据
        String fieldsContainerName =fieldsContainer.getName();
        if(fieldsContainerName.equals("Query")) {
            if(fieldName.equals("auth") )
                return field;
        }
        else if(fieldsContainerName.equals("Mutation")) {
            //特殊接口可直通
            if(fieldName.equals("authenticate") || fieldName.equals("logout") || fieldName.equals("newUser"))
                return field;
        }
        //当前用户权限。
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null){
            Boolean hasRole= auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(defaultRole) );
            return hasRole? field:null;
        }
        else
            return null;   //意味着屏蔽该字段或接口函数。
    }
}
