package org.fjsei.yewu.subscription;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

//后面<Integer>是交换对象类型 ＝＝ 给客户端沟通应答的data模型。
class MyPublisher implements Publisher<Integer> {

  private static final Logger log = LoggerFactory.getLogger(MyPublisher.class);
  //用Flux做的例子　https://github.com/graphql-java-kickstart/graphql-spring-boot/tree/master/example-graphql-subscription
  //很多客户等待中的。激活客户端列表；
  private final List<MySubscription> subscriptions = Collections.synchronizedList(new ArrayList<>());
  private final CompletableFuture<Void> terminated = new CompletableFuture<>();
  private ExecutorService executor = Executors.newFixedThreadPool(4);

  public void waitUntilTerminated() throws InterruptedException {
    try {
      terminated.get();
    } catch (ExecutionException e) {
      System.out.println(e);
    }
  }

  //范型：subscriber类型Subscriber<T>这里<? super Integer> 代表目标实际是Integer的类型。

  @Override
  public void subscribe(Subscriber<? super Integer> subscriber) {
    MySubscription subscription = new MySubscription(subscriber, executor);
    //前端超时会重新尝试，重新尝试还会再来这里，导致subscriptions多次增加了；
    subscriptions.add(subscription);

    subscriber.onSubscribe(subscription);
  }

  //来自底层技术Reactive Stream + concurrent.Flow的接口。
  //对方不需要引用MySubscription，对方只需要获知MyPublisher就行。
  private class MySubscription implements Subscription {

    private final ExecutorService executor;
    private final AtomicInteger value;
    //代表请求的客户端是谁；Subscription只是外层业务代表，subscriber才是最终客户本人；
    private Subscriber<? super Integer> subscriber;
    private AtomicBoolean isCanceled;

    public MySubscription(Subscriber<? super Integer> subscriber, ExecutorService executor) {
      this.subscriber = subscriber;
      this.executor = executor;

      value = new AtomicInteger();
      isCanceled = new AtomicBoolean(false);
    }
   //循环执行，前后端都在轮循，每次都执行。　MySubscriber要主动发起请求的，通过interface就能访问。
    @Override
    public void request(long n) {
      if (isCanceled.get()) {
        //浏览器退出，后面浏览器重新启动没用的。
        return;
      }

      if (n < 0) {
        executor.execute(() -> subscriber.onError(new IllegalArgumentException()));
      } else {
        publishNextItem();
      }
    }

    @Override
    public void cancel() {
      isCanceled.set(true);
      //浏览器关闭时刻来这。
      //前端问题？浏览器失败后，就再也收不到新的重新发起请求的数据或显示。

      synchronized (subscriptions) {
        subscriptions.remove(this);
        if (subscriptions.size() == 0) {
          shutdown();
        }
      }
    }

    private void publishNextItem() {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

      executor.execute(() -> {
        for (int i = 0; i < 100; i++) {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          //都已经shutdown()过了？　，可是这里照样执行。
          int v = value.incrementAndGet();
          log.info("publish item: [{}] with principal {}", v, principal);
       //这里onNext(T t)实际数据类型　value　和　Integer类型的。
       //前端获得应答data.hello就是这里的　v　咯。
          subscriber.onNext(v);
        }
      });
    }

    private void shutdown() {
      log.info("Shut down executor...");
      executor.shutdown();
      newSingleThreadExecutor().submit(() -> {

        log.info("Shutdown complete.");
        terminated.complete(null);
      });
    }

  }

}


//这里技术是：WebFlux响应式编程reactive stream 响应式流; https://blog.csdn.net/dgutliangxuan/article/details/81332794
//WebFlux基于Reactive programming/reactor-core/Netty或者Servlet 3.1容器,(在Web控制端)，它是与SpringMVC来竞争的。
//异步要求，数据库jdbc都得更换了？　com.github.jasync.r2dbc.mysql.JasyncConnectionFactory;　看来WebFlux不能用。
