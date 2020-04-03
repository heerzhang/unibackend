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

@Service
class MySubscriptionResolver implements GraphQLSubscriptionResolver {

  private static final Logger log = LoggerFactory.getLogger(MySubscriptionResolver.class);

  private MyPublisher publisher = new MyPublisher();
  //每个新的前端发起的<Subscription　onHelloIncremented { hello }> 请求到这，随后的循环就省略了。
  //前端超时会重新尝试，重新尝试还会再来认证；
  //必须首先AuthenticationConnectionListener当中认证，才可能允许调用。
  Publisher<Integer> hello(DataFetchingEnvironment env)
  {
    //到这这一步，ws:/握手已经结束，开始发送正常业务数据。
    GraphQLWebSocketContext context = env.getContext();
    Optional<Authentication> authentication = Optional.ofNullable(context.getSession())
        .map(Session::getUserProperties)
        .map(props -> props.get("CONNECT_TOKEN"))
        .map(Authentication.class::cast);
    log.info("Subscribe to publisher with token: {}", authentication);
    authentication.ifPresent(SecurityContextHolder.getContext()::setAuthentication);
    log.info("Security context principal: {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    //认证权限后；

    return publisher;
  }

}
