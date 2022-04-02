package com.example.workingwithtokens.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

@ApiIgnore
@RestController
@RequestMapping("/error")
public class SimpleErrorController extends AbstractController implements ErrorController {


    @RequestMapping
    public ResponseEntity<String> error(HttpServletResponse response) {
        final int status = response.getStatus();
        String responseString = null;
        switch (status) {
            case 400:
                responseString = "Неверный синтаксис";
                break;
            case 401:
                responseString = "Не авторизован";
                break;
            case 403:
                responseString = "Доступ запрещен";
                break;
            case 404:
                responseString = "Не найдено";
                break;
            default:
                responseString = "Ошибка";
        }
        return response(HttpStatus.valueOf(status), "response",responseString);
    }
}