package org.fjsei.yewu.graphql.directive;

import graphql.language.StringValue;
import graphql.schema.*;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import org.fjsei.yewu.graphql.MyDataFetcherFactories;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* directive模型注解： 相对不关心具体的字段或者接口或被注解的内部细节；在外部注入或转换和检查数据，
 注解缺点：很难明确关联，具体细节无法掌握。
模型文件定义如下：
directive @authr(
    qx: [String] = ["USER"]
) on FIELD_DEFINITION
*/

public class AuthrDirective implements SchemaDirectiveWiring {
    //初始化时　解释执行，为每一处的注解@uppercase建立钩子处理函数。
  @Override
  public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> env) {
      GraphQLFieldDefinition graphQLFieldDefinition =env.getElement();
      Set<SimpleGrantedAuthority> requireRoles= new HashSet<SimpleGrantedAuthority>();
      Object listQX = env.getDirective().getArgument("qx").getValue();
      ArrayList<String> roles = (ArrayList<String>)listQX;
      roles.stream().forEach(role -> {
            //若@authr(qx:["null"])就任意登录的都行。
          if(!"null".equals(role))
            requireRoles.add(new SimpleGrantedAuthority("ROLE_" + role));    //该字段所要求的角色之一{最少满足一个吧}
          //authr没有明确指出的其他字段采用缺省角色要求。
      });
      //和WebSecurityConfig相关？没有token登录的也Authenticated! 有anonymousUser 有ROLE_ANONYMOUS的角色；
    GraphQLFieldDefinition field = env.getElement();
    GraphQLFieldsContainer parentType = env.getFieldsContainer();
    // build a data fetcher that transforms the given value to uppercase
    DataFetcher originalFetcher = env.getCodeRegistry().getDataFetcher(parentType, field);
    DataFetcher dataFetcher = MyDataFetcherFactories
            .permitDataFetcher(originalFetcher, ((dataFetchingEnvironment, value) -> {
                if(requireRoles.isEmpty()){
                    Authentication auth= SecurityContextHolder.getContext().getAuthentication();    //当前用户是
                    //TODO：未登录也有auth ANONYMOUS 和配置相关？  而/subscriptions场景却是null;
                    if(auth!=null)  return value;
                    else{
                        throw new IllegalArgumentException(String.format("没登录:针对%s", graphQLFieldDefinition.getName()));
                    }
                }
                else{
                    Authentication auth= SecurityContextHolder.getContext().getAuthentication();
                    if(auth==null)
                        throw new IllegalArgumentException(String.format("没登录:针对%s", graphQLFieldDefinition.getName()));
                    Set<SimpleGrantedAuthority>  权限= new HashSet<SimpleGrantedAuthority>();
                    权限.addAll(requireRoles);
                    权限.retainAll(auth.getAuthorities());
                    if(权限.size() == 0)   //剩下是当前用户能匹配到的权限
                    {
                        throw new IllegalArgumentException(String.format("没权限:针对%s", graphQLFieldDefinition.getName()));
                    }
                }
          return value;
        }));

    // now change the field definition to use the new uppercase data fetcher
    env.getCodeRegistry().dataFetcher(parentType, field, dataFetcher);
    return field;
  }
}


//Scalar是不可再分的基础数据type;
//关注graphql概念：field, Object ,Interface ,Scalar ,Union, InputObject ,Argument; 接口方法也算field的。接口方法不能同名的。
