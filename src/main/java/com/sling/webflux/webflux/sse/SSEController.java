package com.sling.webflux.webflux.sse;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;


/**
 * User: sunling
 * Date: 2023/10/23 14:04
 * Description:
 **/
@Slf4j
@RestController
@CrossOrigin
public class SSEController {

    /**
     * 这里先接收前端发来的sse请求，后续在业务中，调用SinkContainer中的推送方法即可。
     * 1. 注意响应头中要设置text/event-stream。
     * 2. 本例返回值为Flux<String> ， 前后端自由约定数据格式。
     * @param userId
     * @return
     */
    @GetMapping(value = "/stream/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> pusher(@PathVariable("userId") String userId) {

        return Flux.create(sink -> {
            log.info("---> access sse flux request, threadId=" + Thread.currentThread().getId());
            SinkContainer.addClient(userId, sink);

            /**
             * Attaches a LongConsumer to this FluxSink that will be notified of any request to this sink.
             */
//            sink.onRequest(i -> {
//            });

            /**
             * Attach a Disposable as a callback for when this FluxSink is cancelled.
             */
            sink.onCancel(() -> {
                String str = "Flux cancel, threadId=" + Thread.currentThread().getId() + "," + new Date();
                SinkContainer.removeClient(userId);
                log.info(str);
                sink.next(str);
                sink.complete();
            });

            /**
             * Attach a Disposable as a callback for when this FluxSink is effectively disposed,
             * that is it cannot be used anymore.
             */
            sink.onDispose(() -> {
                String str = "Flux dispose, threadId=" + Thread.currentThread().getId() + "," + new Date();
                SinkContainer.removeClient(userId);
                log.info(str);
                sink.next(str);
                sink.complete();
            });
        });
    }

    /**
     * test
     */
    @PostConstruct
    public void pushTest(){
        new Thread(() -> {
            while (true) {
                SinkContainer.push(JSONObject.toJSONString(Arrays.asList("1","11","222")));
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    log.error("push msg error.", e);
                }
            }
        }).start();
    }

    /**
     * test
     */
    @GetMapping("/2")
    public Mono<String> getUser2() { // 支持返回Mono
        return Mono.just("sse");
    }

}
