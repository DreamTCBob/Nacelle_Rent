package com.manager.nacelle_rent.controller;

import io.swagger.annotations.Api;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MessageController {
    //接收客户端发送的消息,当客户端发送消息的目的地为/app/sendTest时，交给该注解所在的方法处理消息
    @MessageMapping("/sendText")
    //修改返回消息的目的地地址为/topic/subscribeTest，经过消息代理，客户端需要订阅了这个主题才能收到返回消息
    @SendTo("/topic/subscribeTest")
    public Map<String, String> post(@Payload Map<String, String> message) {
        message.put("timestamp", Long.toString(System.currentTimeMillis()));
        return message;
    }
    //接收客户端发送的订阅，当客户端订阅的目的地为/subscribeTest时，交给该注解所在的方法处理订阅
    @SubscribeMapping("/subscribeTest")
    public String sub() {
        System.out.print("已订阅");
        return "感谢你订阅了我。。。";
    }
}
