package com.example.workingwithtokens.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.nio.file.AccessDeniedException;

@Slf4j
@Controller
public class TestWSController {
    private SimpMessageSendingOperations operations;

    @Autowired
    public TestWSController(SimpMessagingTemplate template) {
        this.operations = template;
    }

    @Async("schedulePool2")
    @Scheduled(fixedRate = 20000)
    public void testNotification(){
        operations.convertAndSend("/topic/notifications","Test notification");
    }
}
