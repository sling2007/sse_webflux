package com.sling.webflux.webflux;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.websocket.server.PathParam;
import java.util.Date;
import java.util.Random;


/**
 * User: sunling
 * Date: 2023/10/23 14:04
 * Description:
 **/
@RestController
public class WebFluxController {

    @GetMapping("/1")
    public String getUser1() { // 普通响应也没问题
        return "pq1";
    }

    @GetMapping("/2")
    public Mono<String> getUser2() { // 支持返回Mono
        return Mono.just("pq2");
    }

    @GetMapping("/3")
    public Mono<String> getUser3() { // 异步完全没问题
        return Mono.create(sink -> {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {


                }
                sink.success("pq3");
            }).start();
        });
    }

    /**
     * http://localhost:5017/stream
     * 持续10论请求
     *
     * @return
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> flux() {
        return Flux.create(sink -> {
            new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    int tmp = new Random().nextInt(10);
                    try {
                        Thread.sleep(tmp * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(new Date() + "\t\t waited " + tmp + " seconds");
                    sink.next(new Date() + "\t\t waited " + tmp + " seconds");
                }
                System.out.println(new Date() + "\t\t end");
                sink.next(new Date() + "\t\t end");

                sink.complete();
            }).start();
        });
    }

    @GetMapping(value = "/stream/{uid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> flux2(@PathVariable("uid") String uid) {
        return Flux.create(sink -> {
            new Thread(() -> {
                try {
                    while (true) {
                        int tmp = new Random().nextInt(8);
                        try {
                            Thread.sleep(tmp * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        String str = "uid=" + uid + " " + new Date() + "\t\t waited " + tmp + " seconds";
                        System.out.println(str);
                        sink.next(str);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sink.next(new Date() + "\t\t end");
                    sink.complete();
                }
            }).start();
        });
    }

}
