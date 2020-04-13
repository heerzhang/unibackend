package org.fjsei.yewu;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class QueryResolver implements GraphQLQueryResolver {

  public String hello2(String value) {
    return value;
  }

  public double limitedValue(double value) {

    return value*1000.0;

  }
  /*
    type Query {
      hello2(value: String!): String @uppercase
      hello: String  @fetch(from : "zhuanyi")
      limitedValue(value: Float! @range(min: 1.0, max: 999.0)): Float @authr(qx:["Ma"])
      zhuanyi: String
    }
  */

  //必须public的接口函数
  public  String zhuanyi() {
    return "表达女法师的vvsdf";
  }

}
