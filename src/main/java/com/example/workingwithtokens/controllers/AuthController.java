package com.example.workingwithtokens.controllers;

import com.example.workingwithtokens.services.EmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;

@Api(tags="Пути для авторизации")
@RestController
@RequestMapping(produces = "application/json")
@Validated
public class AuthController extends AbstractController {

    @ApiOperation(value = "Регистрация пользователя")
    @PostMapping("/auth/registrate")
    public ResponseEntity<String> registerUser(@Valid @RequestParam("username") String username,
                                               @Valid @RequestParam("password") String password,
                                               @Valid @RequestParam("email") String email,
                                               @Valid @RequestParam("fname") String firstName,
                                               @Valid @RequestParam("lname") String lastName,
                                               @Valid @RequestParam(value = "phone", required = false) String phone) {
        if (userService.findByUsername(username) == null && userService.findByEmail(email) == null) {
            userService.saveUser(username, password, email, lastName, firstName, phone);
            return responseCreated();
        } else {
            return responseBad("response", "Пользователь уже существует");
        }
    }

    @ApiOperation(value = "Авторизация пользователя")
    @PostMapping(value = "/auth/login", produces = "application/json")
    public ResponseEntity<String> auth(@Valid @RequestParam("username") String username,
                                       @Valid @RequestParam("password") String password) {
        if (userService.findByUsernameAndPassword(username, password) != null) {
            String token = jwtProvider.generateToken(username);
            return responseSuccess("response", token);
        }
        return responseBad("response", "Пользователь не существует");
    }
}
