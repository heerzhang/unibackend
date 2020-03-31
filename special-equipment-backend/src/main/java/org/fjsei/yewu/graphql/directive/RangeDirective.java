package org.fjsei.yewu.graphql.directive;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLArgument;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import org.fjsei.yewu.graphql.MyDataFetcherFactories;

/* 代码和模型定义SDL是分离的？，在schema文件里定义如下
directive @range(
    min: Float!,
    max: Float!
) on ARGUMENT_DEFINITION
*/
public class RangeDirective implements SchemaDirectiveWiring {
//关注graphql概念：field, Object ,Interface ,Scalar ,Union, InputObject ,Argument; 接口方法也算field的。接口方法不能同名的。
//底层自带的标识注解；@fetch(from : "otherName") =重定向读取的PropertyDataFecher(); 对模型字段起作用。

  @Override
  public GraphQLArgument onArgument(SchemaDirectiveWiringEnvironment<GraphQLArgument> environment) {
    GraphQLArgument graphQLArgument =environment.getElement();
    //针对模型接口方法的参数注解；在模型文件规定好了，如@range(min: 1.0, max: 999.0)属性取值。
    double min = (double) environment.getDirective().getArgument("min").getValue();
    double max = (double) environment.getDirective().getArgument("max").getValue();

    DataFetcher originalFetcher = environment.getFieldDataFetcher();
    DataFetcher dataFetcher = MyDataFetcherFactories
            .beforeDataFetcher(graphQLArgument.getName(),originalFetcher, ((dataFetchingEnvironment, value) -> {
             //[回调钩子]涉及@range注解的情形；模型接口函数每次实际操作前，都要检查参数。
          if (value instanceof Double && ((double) value < min || (double) value > max)) {
            throw new IllegalArgumentException(
                String.format("参数检查失败 %s is out of range. The range is %s to %s.", value, min, max)
            );
          }
          return value;
        }));
    //每个使用@range注解的参数都会登记这里一次，初始化schema时刻搞。
    environment.getCodeRegistry().dataFetcher(
        environment.getFieldsContainer(),
        environment.getFieldDefinition(),
        dataFetcher
    );
    return environment.getElement();
  }
}
