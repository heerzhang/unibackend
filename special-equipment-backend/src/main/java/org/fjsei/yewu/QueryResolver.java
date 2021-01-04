package org.fjsei.yewu;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

//仅作演示的， 接口

@Service
public class QueryResolver implements GraphQLQueryResolver {

  public String hello2(String value) {
    return value;
  }

  public double limitedValue(double value) {
    return value*1000.0;
  }
  //必须public的接口函数
  public  String zhuanyi() {
    return "表达的vvdf";
  }
}

