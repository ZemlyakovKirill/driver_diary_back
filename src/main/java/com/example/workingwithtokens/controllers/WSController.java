package com.example.workingwithtokens.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class WSController extends AbstractController {

    @MessageMapping("/token/test")
    public void testToken(SimpMessageHeaderAccessor headerAccessor) {
        convertAndSendToSessionJSON(headerAccessor.getSessionId(), "/token/test", "Токен валиден");
    }


}
