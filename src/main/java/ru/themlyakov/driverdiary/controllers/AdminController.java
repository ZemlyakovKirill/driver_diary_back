package ru.themlyakov.driverdiary.controllers;

import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@Api(tags = "Пути администратора")
@RequestMapping(produces = "application/json")
@Validated
public class AdminController extends AbstractController {


    @GetMapping("/admin/all/users")
    public ResponseEntity<String> getAllUsers(HttpServletResponse servletResponse) {
        return responseSuccess("status", "200", "response", userService.findAll());
    }
}
