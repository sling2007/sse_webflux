package com.sling.webflux.webflux;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * User: sunling
 * Date: 2023/10/23 14:04
 * Description:
 **/
@Slf4j
@RestController
@CrossOrigin
public class WebFluxController {

    public static ConcurrentHashMap<String, FluxSink<String>> clients = new ConcurrentHashMap<>();

    @GetMapping(value = "/streamWithId/{uuid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamWithId(@PathVariable("uuid") String uuid) {

        return Flux.create(sink -> {
            log.info("---> access sse flux request, threadId=" + Thread.currentThread().getId());
            clients.put(uuid, sink);
//            sink.onRequest(i -> {
//                log.info("sse flux request on , threadId=" + Thread.currentThread().getId());
//
//                new Thread(() -> {
//                    for (int j = 0; j < i; j++) {
//                        try {
//                            String msg = "--->heartbeat, threadId=" + Thread.currentThread().getId() + "," + new Date();
//                            sink.next(msg);
//                            log.info(msg);
//                            Thread.sleep(3000L);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
//            });

            sink.onCancel(() -> {
                String str = "Flux cancel, threadId=" + Thread.currentThread().getId() + "," + new Date();
                log.info(str);
                sink.next(str);
                sink.complete();
                clients.remove(uuid);
            });
            sink.onDispose(() -> {
                String str = "Flux dispose, threadId=" + Thread.currentThread().getId() + "," + new Date();
                log.info(str);
                sink.next(str);
                sink.complete();
                clients.remove(uuid);
            });
        });
    }


    @PostConstruct
    public void sendWhenBizNeed() {

        new Thread(() -> {
            while (true) {

                for (Map.Entry<String, FluxSink<String>> entry : clients.entrySet()) {
                    String key = entry.getKey();
                    FluxSink<String> sink = entry.getValue();
                    String msg = "------------------------------------->biz info pushing, for userId = " + key + ", threadId=" + Thread.currentThread().getId() + ", date=" + new Date();
                    log.info(msg);
                    sink.next(msg);
                }

                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream2() {
        return Flux.create(this::process);
    }

    private void process(final FluxSink<String> sink) {
        log.info("access sse flux request, threadId=" + Thread.currentThread().getId());
        final AtomicBoolean isStarted = new AtomicBoolean(true);

        sink.onRequest(i -> {
            log.info("sse flux request on , threadId=" + Thread.currentThread().getId());
            new Thread(() -> {
                for (int j = 0; j < i && isStarted.get(); j++) {
                    try {
                        List<ElectronicAlarmLogVO> alarms = mockAlarms();

                        String str = "";
                        if (alarms.size() == 0) {
                        } else {
                            str = JSONObject.toJSONString(alarms);
                            alarms.clear();
                        }

                        sink.next(str);
                        log.info("------webflux推送告警数据, threadId=" + Thread.currentThread().getId() + ", " + str);
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
            log.info(str);
            sink.next(str);
            sink.complete();
        });
        sink.onDispose(() -> {
            isStarted.set(false);
            String str = "Flux dispose, threadId=" + Thread.currentThread().getId() + "," + new Date() + " , isStarted=" + isStarted.get();
            log.info(str);
            sink.next(str);
            sink.complete();
        });
    }

    private List<ElectronicAlarmLogVO> mockAlarms() {
        List<ElectronicAlarmLogVO> alarms = new ArrayList<>();

        Random random = new Random();
        int r = random.nextInt(3);

        for (int tmp = 0; tmp % 2 == 0 && tmp < r; tmp++) {
            ElectronicAlarmLogVO vo = new ElectronicAlarmLogVO();

            vo.setAlarmLevel(r);
            vo.setId(tmp + "");
            vo.setDefenseSectionId("defsectionid");
            vo.setName("name");
            vo.setSource("source");
            vo.setOccurTime(new Date().toString());
            vo.setTitle("title");

            alarms.add(vo);
        }
        return alarms;
    }


    @GetMapping("/1")
    public String getUser1() { // 普通响应也没问题
        return "pq1";
    }

    @GetMapping("/2")
    public Mono<String> getUser2() { // 支持返回Mono
        return Mono.just("pq2");
    }

}
