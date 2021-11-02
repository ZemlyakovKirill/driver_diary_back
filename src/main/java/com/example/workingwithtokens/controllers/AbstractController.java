package com.example.workingwithtokens.controllers;

import com.example.workingwithtokens.providers.JwtProvider;
import com.example.workingwithtokens.repositories.RequestMarkRepository;
import com.example.workingwithtokens.repositories.UserVehicleRepository;
import com.example.workingwithtokens.repositories.VehicleCostsRepository;
import com.example.workingwithtokens.repositories.VehicleRepository;
import com.example.workingwithtokens.services.UserService;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ControllerAdvice
public class AbstractController {
    @Autowired
    RequestMarkRepository requestMarkRepository;

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    UserVehicleRepository userVehicleRepository;

    @Autowired
    VehicleCostsRepository vehicleCostsRepository;

    @Autowired
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    protected UserService userService;
    @Autowired
    protected JwtProvider jwtProvider;

    protected static final Gson json = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

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

    public static ResponseEntity<String> responseForbidden(Object... response) {
        return response(HttpStatus.FORBIDDEN, response);
    }

    public static ResponseEntity<String> responseBad(Object... response) {
        return response(HttpStatus.BAD_REQUEST, response);
    }

    public static ResponseEntity<String> responseCreated(Object... response) {
        return response(HttpStatus.CREATED, response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> validationHandler(Exception e) {
        if (e instanceof ConstraintViolationException e1) {
            Set<String> reasons = new HashSet<>();
            System.out.println(e1.getLocalizedMessage());
            e1.getConstraintViolations().forEach(cv -> reasons.add(cv.getMessage()));
            return responseBad("response", reasons);
        }
        return responseBad("response",e.getMessage());
    }

}

