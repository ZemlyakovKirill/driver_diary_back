package com.example.workingwithtokens.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/auth", produces = "application/json")
@Validated
public class AuthController extends AbstractController {
    @PostMapping("/registrate")
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

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<String> auth(@Valid @RequestParam("username") String username,
                                       @Valid @RequestParam("password") String password) {
        if (userService.findByUsernameAndPassword(username, password) != null) {
            String token = jwtProvider.generateToken(username);
            return responseSuccess("token", token);
        }
        return responseBad("response", "Пользователь не существует");
    }
}
