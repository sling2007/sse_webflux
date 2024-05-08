package com.sling.webflux.webflux.sse;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.ConcurrentHashMap;

/**
 * User: sunling
 * Date: 2023/10/23 18:36
 * Description:
 **/
@Slf4j
public class SinkContainer {

    /**
     * 饿汉单例模式，持有sinks
     */
    private static ConcurrentHashMap<String, FluxSink<String>> clients = new ConcurrentHashMap<>(16);

    /**
     * 单例模式，防止实例化
     */
    private SinkContainer(){
    }

    /**
     * 获取所有连接
     * @return
     */
    private static ConcurrentHashMap<String, FluxSink<String>> getClients(){
        return clients;
    }

    /**
     * 获取指定连接
     * @param userId
     * @return
     */
    public static FluxSink<String> getClientByUserId(String userId){
        if(userId == null){
            throw new RuntimeException();
        }
        return getClients().get(userId);
    }

    /**
     * 增加连接
     * @param userId
     * @param sink
     */
    public static void addClient(String userId, FluxSink<String> sink){
        if(userId == null || sink == null){
            throw new RuntimeException();
        }
        getClients().put(userId, sink);
        log.info("addClient, userId={}", userId);
        log.info("after addClient, clients are {}", getClients().keySet());
    }

    /**
     * 回收指定连接
     * @param userId
     */
    public static void removeClient(String userId){
        if(userId == null){
            throw new RuntimeException();
        }
        getClients().remove(userId);
        log.info("removeClient, userId={}", userId);
        log.info("after removeClient, clients are {}", getClients().keySet());
    }

    /**
     * clear
     */
    public static void clear(){
        getClients().clear();
    }

    /**
     * 推送指定用户
     * @param userId
     * @param msg
     */
    public static void push(String userId, String msg){
        if(log.isDebugEnabled()){
            log.debug("sse push, userId={}, msg={}", userId, msg);
        }
        if(userId == null){
            throw new RuntimeException();
        }
        getClientByUserId(userId).next(msg);
    }

    /**
     * 推送所有用户连接
     * @param msg
     */
    public static void push(String msg){
        getClients().entrySet().stream().forEach(entry -> push(entry.getKey(), msg));
    }
}
