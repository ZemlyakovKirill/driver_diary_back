package com.example.workingwithtokens.controllers;

import com.example.workingwithtokens.entities.User;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/google", produces = "application/json")
public class GoogleAuthController extends AbstractController {

    @Autowired
    private Environment environment;

    @RequestMapping("/auth")
    public RedirectView auth() {
        final String port = environment.getProperty("server.port");
        final String address = environment.getProperty("redirect.address-for-oauth");
        return new RedirectView(
                "https://accounts.google.com/o/oauth2/v2/auth" +
                        "?client_id=" + environment.getProperty("google.client-id") +
                        "&redirect_uri=https://" + address + ".xip.io:" + port + "/google/accessing" +
                        "&response_type=code" +
                        "&scope=https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email"
        );
    }

    @RequestMapping("/accessing")
    public ResponseEntity<String> accessTokenRecieving(@RequestParam("code") String code) throws IOException {
        final String port = environment.getProperty("server.port");
        final String address = environment.getProperty("redirect.address-for-oauth");
        HttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://oauth2.googleapis.com/token");
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("code", code));
        params.add(new BasicNameValuePair("client_id", environment.getProperty("google.client-id")));
        params.add(new BasicNameValuePair("client_secret", environment.getProperty("google.secret")));
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("redirect_uri", "https://" + address + ".xip.io:" + port + "/google/accessing"));
        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        HttpResponse response = client.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();

        if (responseEntity != null) {
            try (InputStream instream = responseEntity.getContent()) {
                final String access_token = httpResponseReader(instream, "access_token").get("access_token");
                HttpGet httpGet = new HttpGet("https://www.googleapis.com/oauth2/v1/userinfo" +
                        "?alt=json" +
                        "&access_token=" + access_token);
                response = client.execute(httpGet);
                responseEntity = response.getEntity();
                if (responseEntity != null) {
                    try (InputStream instream2 = responseEntity.getContent()) {
                        final Map<String, String> attributes = httpResponseReader(instream2, "email", "family_name", "given_name");
                        return registrateGoogleUser(attributes.get("family_name"),
                                attributes.get("given_name"),
                                attributes.get("email"));
                    }
                }
            }
        }
        return responseBad("response", "Response is null");
    }

    public ResponseEntity<String> registrateGoogleUser(String lastName, String firstName, String email) {
        final String username = email.split("@")[0];
        if (userService.findByUsernameWithoutVk(username) == null) {
            if (firstName.matches("[A-ZА-Я][a-zа-я]{1,99}") &&
                    lastName.matches("[A-ZА-Я][a-zа-я]{1,99}")) {
                userService.saveUserGoogle(username, email, lastName, firstName);
            }
            else
                return responseBad("response","Last name or First name is invalid");
        }
        return loginGoogleUser(username);
    }

    public ResponseEntity<String> loginGoogleUser(String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            String token = jwtProvider.generateToken(username);
            return responseSuccess("response", token);
        } else
            return responseBad("response", "User not found");
    }
}
