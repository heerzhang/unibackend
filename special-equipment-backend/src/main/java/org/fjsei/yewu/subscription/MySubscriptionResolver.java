package org.fjsei.yewu.subscription;

import graphql.kickstart.tools.GraphQLSubscriptionResolver;
import graphql.schema.DataFetchingEnvironment;
import graphql.servlet.context.GraphQLWebSocketContext;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.Optional;

//配套Subscription前端，必要求集成性，不能分开多个的endpoint接口，不然需要多个qq窗口接入才能服务和交流信息。不能像Query/Mutation那样搞多个安全域概念。
//订阅功能Subscription：主动权在服务器端，安全性较易控制，再说内容发布的对象模型涉猎地比较局部化。Subscription和graphql本质上servlet完全分开的。

@Service
class MySubscriptionResolver implements GraphQLSubscriptionResolver {
  private static final Logger log = LoggerFactory.getLogger(MySubscriptionResolver.class);

  private MyPublisher publisher = new MyPublisher();
  //每个新的前端发起的<Subscription　onHelloIncremented { hello }> 请求到这，随后的循环就省略了。
  //前端超时会重新尝试，重新尝试还会再来认证；
  //必须首先AuthenticationConnectionListener当中认证，才可能允许调用。
  Publisher<Integer> hello(DataFetchingEnvironment env)
  {
    Authentication auth= SecurityContextHolder.getContext().getAuthentication();
    if(auth==null){
      //到这这一步，ws:/握手已经结束，开始发送正常业务数据。
      //权限认证实际在前面的步骤。 没有登录的用户实际上publisher不会去执行的！。
      return publisher;
      /*      GraphQLWebSocketContext context = env.getContext();
       Optional<Authentication> authentication = Optional.ofNullable(context.getSession())
              .map(Session::getUserProperties)
              .map(props -> props.get("CONNECT_TOKEN"))
              .map(Authentication.class::cast);
        log.info("Subscribe to publisher with token: {}", authentication);
        仅在握手时有authentication认证，正常数据包无法得知认证信息。
        authentication.ifPresent(SecurityContextHolder.getContext()::setAuthentication);
      */
    }
    else {
      log.info("Security context principal: {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
    //后；发布开始。
    //从这里开始ws://协议层次和 publisher 一点关系都没有?。return null;并不意味ws:/连接要关闭的。
    return publisher;
  }
  //模型定义文件都是在graphql主安全域之内。
  //若有必须是最后一个参数[graphql.schema.DataFetchingEnvironment]
  Publisher<Integer> qqCommunicate(DataFetchingEnvironment env)
  {
    Authentication auth= SecurityContextHolder.getContext().getAuthentication();
    if(auth==null){
      //作废！   到这这一步，ws:/握手已经结束，开始发送正常业务数据。
      //权限认证实际在前面的步骤。
      GraphQLWebSocketContext context = env.getContext();
      Optional<Authentication> authentication = Optional.ofNullable(context.getSession())
              .map(Session::getUserProperties)
              .map(props -> props.get("CONNECT_TOKEN"))
              .map(Authentication.class::cast);
      log.info("Subscribe to publisher with token: {}", authentication);
      //仅在握手时有authentication认证，正常数据包无法得知认证信息。
      authentication.ifPresent(SecurityContextHolder.getContext()::setAuthentication);
    }
    log.info("Security context principal: {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    //后；发布开始。
    //从这里开始ws://协议层次和 publisher 一点关系都没有?。return null;并不意味ws:/连接要关闭的。
    return publisher;
  }
}

