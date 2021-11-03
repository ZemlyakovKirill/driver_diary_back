package com.example.workingwithtokens.controllers;

import com.example.workingwithtokens.entities.News;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/auth", produces = "application/json")
@Validated
public class AuthController extends AbstractController {
    @RequestMapping("/registrate")
    public ResponseEntity<String> registerUser(@RequestParam("username") String username,
                                               @RequestParam("password") @Valid String password,
                                               @Valid @RequestParam("email") String email,
                                               @RequestParam("fname") @Valid String firstName,
                                               @RequestParam("lname") @Valid String lastName,
                                               @RequestParam(value = "phone",required = false,defaultValue = "not set") @Valid String phone,
                                               HttpServletResponse response) {
        if (userService.findByUsername(username) == null && userService.findByEmail(email)==null) {
            userService.saveUser(username, password,email,lastName,firstName,phone);
            return responseCreated();
        } else
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return responseBad("message", "Wrong User Data");
    }

    @RequestMapping(value = "/login", produces = "application/json")
    public ResponseEntity<String> auth(@RequestParam("username") String username,
                                       @RequestParam("password") String password) {
        if (userService.findByUsernameAndPassword(username, password) != null) {
            String token = jwtProvider.generateToken(username);
            return responseSuccess("token", token);
        }
        return responseBad("message", "Bad user data");
    }
}
