package com.sling.webflux.webflux.test;

/**
 * User: sunling
 * Date: 2024/4/12 11:35
 * Description:
 **/
public interface MyEventListener {
    void onNewEvent(MyEventSource.MyEvent event);
    void onEventStopped();
}