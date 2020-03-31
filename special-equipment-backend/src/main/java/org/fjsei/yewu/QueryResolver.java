package org.fjsei.yewu;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
//@Component
public class QueryResolver implements GraphQLQueryResolver {
  String hello() {
    return "wor看电话ld";
  }
  public String hello2(String value) {
    return value;
  }

  public double limitedValue(double value) {

    return value*1000.0;

  }

  String zhuanyi() {
    return "表达女法师的vvsdf";
  }
}
