package org.fjsei.yewu.graphql;

import graphql.PublicApi;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetcherFactory;
import graphql.schema.DataFetchingEnvironment;

import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;


//修改底层

/**
 * A helper for {@link graphql.schema.DataFetcherFactory}
 */
@PublicApi
public class MyDataFetcherFactories {

    /**
     * This helper function allows you to wrap an existing data fetcher and map the value once it completes.  It helps you handle
     * values that might be {@link  java.util.concurrent.CompletionStage} returned values as well as plain old objects.
     *
     * @param delegateDataFetcher the original data fetcher that is present on a {@link graphql.schema.GraphQLFieldDefinition} say
     * @param mapFunction     是回调钩子，不会立刻执行的。
     *
     * @return a new data fetcher that wraps the provided data fetcher
    底层有错误！
     用于 onArgument @directive 注入; 参数场景。
    在DataFetcher执行之前率先 执行钩子函数。
     */
    public static DataFetcher beforeDataFetcher(String argumentName,DataFetcher delegateDataFetcher, BiFunction<DataFetchingEnvironment, Object, Object> mapFunction) {
        return environment -> {
            //String argumentName=environment.getFieldDefinition().getArguments().get(0).getName();
            //Boolean matched=environment.getFieldDefinition().getArguments().get(0).getDirectives().get(0).getName().equals(directiveName);
             Object argumentValue =environment.getArgument( argumentName );
              //检查回调函数
             Object retArgumentValue =mapFunction.apply(environment, argumentValue);
            //［用途］针对Directive时刻的；就不能修改入口输入参数传递的值，只能对参数进行检查，抛出异常。

            //真正to fetch the value从ORM数据库取得。
            Object value = delegateDataFetcher.get(environment);
            if (value instanceof CompletionStage) {
                //return ((CompletionStage<Object>) value).thenApply(v -> mapFunction.apply(environment, v));
                return ((CompletionStage<Object>) value).thenApply(v -> v);
            } else {
                //return mapFunction.apply(environment, value);
                return value;
            }
        };
    }

    //权限检查版本的。
    public static DataFetcher permitDataFetcher(DataFetcher delegateDataFetcher, BiFunction<DataFetchingEnvironment, Object, Object> mapFunction) {
        return environment -> {
            //检查回调函数
            Object retArgumentValue =mapFunction.apply(environment, null);
            //［用途］针对Directive时刻的；就不能修改入口输入参数传递的值，只能对参数进行检查，抛出异常。

            //真正to fetch the value从ORM数据库取得。
            Object value = delegateDataFetcher.get(environment);
            if (value instanceof CompletionStage) {
                //return ((CompletionStage<Object>) value).thenApply(v -> mapFunction.apply(environment, v));
                return ((CompletionStage<Object>) value).thenApply(v -> v);
            } else {
                //return mapFunction.apply(environment, value);
                return value;
            }
        };
    }
}
