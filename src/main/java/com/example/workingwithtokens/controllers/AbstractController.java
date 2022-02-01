package com.example.workingwithtokens.controllers;

import com.example.workingwithtokens.providers.JwtProvider;
import com.example.workingwithtokens.repositories.*;
import com.example.workingwithtokens.services.UserService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class AbstractController {
    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    RequestMarkRepository requestMarkRepository;

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    UserNewsRepository newsRepository;

    @Autowired
    UserVehicleRepository userVehicleRepository;

    @Autowired
    VehicleCostsRepository vehicleCostsRepository;

    @Autowired
    UserNoteRepository userNoteRepository;

    @Autowired
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    protected UserService userService;
    @Autowired
    protected JwtProvider jwtProvider;

    protected static final Gson json = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();


    protected void convertAndSendJSON(String destination,Object payload){
        Map<String, Object> responseMap = new HashMap<>();
        Map<String,Object> headers=new HashMap<>();
        headers.put("status",200);
        responseMap.put("response",payload);
        template.convertAndSend(destination,json.toJson(responseMap),headers);
    }

    protected void convertAndSendToUserJSON(String username,String destination,Object payload){
        Map<String, Object> responseMap = new HashMap<>();
        Map<String,Object> headers=new HashMap<>();
        headers.put("status",200);
        responseMap.put("response",payload);
        template.convertAndSendToUser(username,destination,json.toJson(responseMap),headers);
    }

    public static ResponseEntity<String> response(HttpStatus status, Object... response) {
        Map<String, Object> responseMap = new HashMap<>();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        responseMap.put("status", status.value());
        for (int i = 0; i < response.length; i += 2) {
            responseMap.put(response[i].toString(), response[i + 1]);
        }
        return new ResponseEntity<>(json.toJson(responseMap), headers, status);
    }
    public static Map<String,String> httpResponseReader(InputStream inputStream,String ... executingStrings) throws IOException {
        JsonObject jobj=new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();
        Map<String,String> response=new HashMap<>();
        for (String executingString : executingStrings) {
            response.putIfAbsent(executingString,
                    jobj.has(executingString)?
                            jobj.get(executingString).getAsString()
                            :null);
        }
        return response;
    }

    public static String responseString(HttpStatus status, Object... response) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", status.value());
        for (int i = 0; i < response.length; i += 2) {
            responseMap.put(response[i].toString(), response[i + 1]);
        }
        return response(status, response).getBody();
    }

    public static ResponseEntity<String> responseSuccess(Object... response) {
        return response(HttpStatus.OK, response);
    }


    public static ResponseEntity<String> responseBad(Object... response) {
        return response(HttpStatus.BAD_REQUEST, response);
    }

    public static ResponseEntity<String> responseCreated(Object... response) {
        return response(HttpStatus.CREATED, response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> validationHandler(Exception e) {
        if (e instanceof ConstraintViolationException) {
            StringBuilder reasons = new StringBuilder();
            ((ConstraintViolationException) e).getConstraintViolations().forEach(cv -> reasons.append(cv.getMessage()).append(", "));
            if(reasons.length()>=2){
                reasons.delete(
                        reasons.length()-2,
                        reasons.length()-1
                );
            }
            return responseBad("response", reasons);
        }
        return responseBad("response",e.getMessage());
    }

}

