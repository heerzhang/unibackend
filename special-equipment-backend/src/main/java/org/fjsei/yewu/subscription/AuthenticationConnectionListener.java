package org.fjsei.yewu.subscription;

import graphql.kickstart.execution.subscriptions.SubscriptionSession;
import graphql.kickstart.execution.subscriptions.apollo.ApolloSubscriptionConnectionListener;
import graphql.kickstart.execution.subscriptions.apollo.OperationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Map;

//首先要过WebSecurityConfigurerAdapter验证，然后才来这里的。

@Component
class AuthenticationConnectionListener implements ApolloSubscriptionConnectionListener {

  private static final Logger log = LoggerFactory.getLogger(AuthenticationConnectionListener.class);
  //每个用户请求初始化的；ws://超时之后重新连接也来这里的。
  public void onConnect(SubscriptionSession session, OperationMessage message) {
    log.debug("onConnect with payload {}", message.getPayload().getClass());
    //相当于token口令，代表认证账户。
    String token = ((Map<String, String>) message.getPayload()).get("authToken");
    log.info("Token: {}", token);
    Authentication authentication = new UsernamePasswordAuthenticationToken(token, null);
    session.getUserProperties().put("CONNECT_TOKEN", authentication);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    //subscriptions认证 无法通过当前设定安全框架。

  }
}

