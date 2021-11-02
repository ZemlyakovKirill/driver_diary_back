package com.example.workingwithtokens.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping(value = "/admin",produces = "application/json")
@Validated
public class AdminController extends AbstractController {

    @RequestMapping("/all/users")
    public ResponseEntity<String> getAllUsers(HttpServletResponse servletResponse) {
        return responseSuccess("status", "200", "response", userService.findAll());
    }
}
