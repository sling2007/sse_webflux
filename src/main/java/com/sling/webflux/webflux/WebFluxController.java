package com.sling.webflux.webflux;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * User: sunling
 * Date: 2023/10/23 14:04
 * Description:
 **/
@RestController
@CrossOrigin
public class WebFluxController {


    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream2() {
        return Flux.create(this::process);
    }

    private void process(final FluxSink<String> sink) {
        System.out.println("access sse flux request");
        final AtomicBoolean isStarted = new AtomicBoolean(true);

        sink.onRequest(i -> {
            new Thread(() -> {
                for (int j = 0; j < i && isStarted.get(); j++) {
                    try {
                        List<ElectronicAlarmLogVO> alarms = new ArrayList<>();

                        Random random = new Random();;
                        int r = random.nextInt(3);

                        for(int tmp = 0; j%6==0 && tmp < r; tmp++){
                            ElectronicAlarmLogVO vo = new ElectronicAlarmLogVO();

                            vo.setAlarmLevel(j);
                            vo.setId(tmp + "");
                            vo.setDefenseSectionId("defsectionid");
                            vo.setName("name");
                            vo.setSource("source");
                            vo.setOccurTime(new Date().toString());
                            vo.setTitle("title");

                            alarms.add(vo);
                        }
                        String str = "";

                        if (alarms.size() == 0) {
                        }else {
                            str = JSONObject.toJSONString(alarms);
                            alarms.clear();
                        }

                        sink.next(str);
                        System.out.println("------webflux推送告警数据：" + str);
                        Thread.sleep(2000L);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        });

        sink.onCancel(() -> {
            isStarted.set(false);
            String str = "Flux cancel, threadId=" + Thread.currentThread().getId() + "," + new Date() + " , isStarted=" + isStarted.get();
            System.out.println(str);
            sink.next(str);
            sink.complete();
        });
        sink.onDispose(() -> {
            isStarted.set(false);
            String str = "Flux dispose, threadId=" + Thread.currentThread().getId() + "," + new Date() + " , isStarted=" + isStarted.get();
            System.out.println(str);
            sink.next(str);
            sink.complete();
        });
    }


    /**
     * http://localhost:5017/stream
     * 持续10论请求
     *
     * 在/resource/static/s.html中，有发起这个webflux的sse请求的前端代码，可以参照。
     * @return
     */
    @GetMapping(value = "/stream1", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
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


    /**
     * http://localhost:5017/stream/user123
     * 死循环推送数据
     *
     * 在/resource/static/s.html中，有发起这个webflux的sse请求的前端代码，可以参照。
     * @return
     */
    @GetMapping(value = "/stream1/{uid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
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
}
