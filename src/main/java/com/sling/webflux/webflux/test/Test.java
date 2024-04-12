package com.sling.webflux.webflux.test;

import reactor.core.publisher.Flux;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * User: sunling
 * Date: 2024/4/12 11:32
 * Description:
 **/
public class Test {
    public static void main(String[] args) throws InterruptedException {
        test1();
    }

    private static void test1() throws InterruptedException {
        MyEventSource eventSource = new MyEventSource();    // 1

        Flux.create(sink -> {
                    eventSource.register(new MyEventListener() {     // 2
                        @Override
                        public void onNewEvent(MyEventSource.MyEvent event) {
                            sink.next(event);       // 3
                        }

                        @Override
                        public void onEventStopped() {
                            sink.complete();        // 4
                        }
                    });
                }
        ).subscribe(System.out::println);       // 5

        for (int i = 0; i < 10; i++) {   // 6
            Random random = new Random();
            TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
            eventSource.newEvent(new MyEventSource.MyEvent(new Date(), "Eventttttt-" + i));
        }
        eventSource.eventStopped(); // 7
    }
}
