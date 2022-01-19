package com.example.workingwithtokens.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class TestWSController {
    private SimpMessagingTemplate template;

    @Autowired
    public TestWSController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(String message) {
        log.info("Hello");
        return "Hello " + message;
    }

    @Async("schedulePool2")
    @Scheduled(fixedRate = 20000)
    public void test(){
        template.convertAndSend("/topic/greetings","Hello this is cycle");
    }
}
