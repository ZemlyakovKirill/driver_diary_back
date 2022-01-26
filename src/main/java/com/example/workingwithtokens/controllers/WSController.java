package com.example.workingwithtokens.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
public class WSController extends AbstractController {

    @MessageMapping("/token/test")
    public void testToken(Principal principal) {
        convertAndSendToUserJSON( principal.getName(), "/token/test","Токен валиден");
    }


}
