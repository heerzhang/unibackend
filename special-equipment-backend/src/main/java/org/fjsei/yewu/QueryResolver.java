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
  public String hello(String value) {
    return value;
  }

  public double limitedValue(double value) {

    return value;

  }
}
