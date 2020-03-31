package org.fjsei.yewu.graphql.directive;

import graphql.schema.*;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;

/* directive模型注解： 相对不关心具体的字段或者接口或被注解的内部细节；在外部注入或转换和检查数据，
 注解缺点：很难明确关联，具体细节无法掌握。
模型文件定义如下：
directive @uppercase on FIELD_DEFINITION
*/
public class UppercaseDirective implements SchemaDirectiveWiring {
    //初始化时　解释执行，为每一处的注解@uppercase建立钩子处理函数。
  @Override
  public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> env) {
    GraphQLFieldDefinition field = env.getElement();
    GraphQLFieldsContainer parentType = env.getFieldsContainer();
    // build a data fetcher that transforms the given value to uppercase
    DataFetcher originalFetcher = env.getCodeRegistry().getDataFetcher(parentType, field);
    DataFetcher dataFetcher = DataFetcherFactories
            .wrapDataFetcher(originalFetcher, ((dataFetchingEnvironment, value) -> {
          if (value instanceof String) {
            return ((String) value).toUpperCase();
          }
          return value;
        }));

    // now change the field definition to use the new uppercase data fetcher
    env.getCodeRegistry().dataFetcher(parentType, field, dataFetcher);
    return field;
  }
}
