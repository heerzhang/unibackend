package org.fjsei.yewu.subscription;

import graphql.kickstart.execution.subscriptions.SubscriptionSession;
import graphql.kickstart.execution.subscriptions.apollo.ApolloSubscriptionConnectionListener;
import graphql.kickstart.execution.subscriptions.apollo.OperationMessage;
import org.fjsei.yewu.service.security.JwtUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import java.util.Map;

//首先要过WebSecurityConfigurerAdapter验证，然后才来这里的。Http与websocket是两个完全不同的协议，两者唯一联系是WebSocket利用Http进行握手；

@Component
class AuthenticationConnectionListener implements ApolloSubscriptionConnectionListener {
  @Autowired
  private JwtUserDetailsService jwtUserDetailsService;

  private static final Logger log = LoggerFactory.getLogger(AuthenticationConnectionListener.class);
  //每个用户请求初始化的；ws://超时之后重新连接也来这里的。

  //首先MyCorsConfig指示CORSfilter过关,然后JwtAuthorizationTokenFilter过一遍,最后才到这里：
  //前面处理还没结束，同一个客户无法再次连接到这里来的。
  public void onConnect(SubscriptionSession session, OperationMessage message) {
    //无法掌控http握手。运行到了这一步实际上http握手早就完成了，这里也无法获知http的相关cookie等交互信息。想认证http白搭。
    log.debug("onConnect with payload {}", message.getPayload().getClass());
    //相当于token口令，代表认证账户。
    String token = ((Map<String, String>) message.getPayload()).get("authToken");
    log.info("Token: {}", token);
    //WebSocket认证特别，让Http认证过后，格外申请个一次性token并且特别用途目的coockieToken,让JS可以读取。
    //挂token名
    UserDetails userDetails = jwtUserDetailsService.loadUserByUsername( "herzhang" );
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());


    //  authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(null));
    //  SecurityContextHolder.getContext().setAuthentication(authentication);

   // Authentication authentication = new UsernamePasswordAuthenticationToken(token, null);

    session.getUserProperties().put("CONNECT_TOKEN", authentication);
    //保存CONNECT_TOKEN起来再在Publisher<Integer> hello(DataFetchingEnvironment env)把身份信息取得。？？
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}

//nginx需要做一些配置，支持websocket通信。  https://blog.csdn.net/qq_34912469/article/details/94006301
