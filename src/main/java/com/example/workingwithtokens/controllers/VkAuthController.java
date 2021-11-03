package com.example.workingwithtokens.controllers;

import com.example.workingwithtokens.entities.User;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping(value = "/vk", produces = "application/json")
public class VkAuthController extends AbstractController {
    @Autowired
    private Environment environment;

    @RequestMapping("/auth")
    public RedirectView registrateViaVk() {
        final String port = environment.getProperty("server.port");
        final String address = environment.getProperty("redirect.address-for-oauth");
        return new RedirectView(
                "https://oauth.vk.com/authorize" +
                        "?client_id=" + environment.getProperty("vk.client-id") +
                        "&scope=email" +
                        "&v=5.59" +
                        "&redirect_uri=https://" + address + ':' + port + "/vk/accessing"
        );
    }

    //https://api.vk.com/method/users.get?user_ids=210700286&fields=bdate&access_token=533bacf01e11f55b536a565b57531ac114461ae8736d6506a3&v=5.131
    @RequestMapping("/accessing")
    public ResponseEntity<String> accessTokenRecieving(@RequestParam(value = "code", required = false) String code,
                                                       @RequestParam(value = "error", required = false) String error,
                                                       @RequestParam(value = "error_reason", required = false) String reason,
                                                       @RequestParam(value = "error_description", required = false) String desc) throws IOException {


        if (error == null && !code.isEmpty()) {
            final String address = environment.getProperty("redirect.address-for-oauth");
            final String port = environment.getProperty("server.port");
            HttpClient client = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(String.format("https://oauth.vk.com/token" +
                            "?code=%s" +
                            "&client_id=%s" +
                            "&client_secret=%s" +
                            "&redirect_uri=https://%s:%s/vk/accessing",
                    code, environment.getProperty("vk.client-id"),
                    environment.getProperty("vk.secret"), address, port)
            );
            HttpResponse response = client.execute(httpGet);
            HttpEntity responseEntity = response.getEntity();
            BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
            Map<String, String> attributes = httpResponseReader(responseEntity.getContent(), "access_token", "user_id", "email");
            final String access_token = attributes.get("access_token");
            final String user_id = attributes.get("user_id");
            final String email = attributes.get("email").toLowerCase(Locale.ROOT);
            if (email == null) {
                return responseBad("response", "Email has to be not null");
            }
            HttpPost httpPost = new HttpPost("https://api.vk.com/method/users.get");
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("user_ids", user_id));
            params.add(new BasicNameValuePair("fields", "screen_name,mobile_phone"));
            params.add(new BasicNameValuePair("access_token", access_token));
            params.add(new BasicNameValuePair("v", "5.131"));

            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            response = client.execute(httpPost);
            responseEntity = response.getEntity();
            JsonObject responseUserData = new JsonParser().parse(new InputStreamReader(responseEntity.getContent())).
                    getAsJsonObject().get("response").getAsJsonArray().get(0).getAsJsonObject();
            final String last_name = responseUserData.get("last_name").getAsString();
            final String first_name = responseUserData.get("first_name").getAsString();
            final String screen_name = responseUserData.get("screen_name").getAsString();

            return registrateUserVk(screen_name, last_name, first_name, email);

        }
        return responseBad("error", error,
                "reason", reason,
                "message", desc);
    }

    private ResponseEntity<String> registrateUserVk(String username, String last_name, String first_name, String email) {
        System.out.println(username);
        System.out.println(last_name);
        System.out.println(first_name);
        System.out.println(email);
        if (userService.findByUsernameWithoutGoogle(username) == null) {
            userService.saveUserVk(
                    username,
                    email,
                    last_name,
                    first_name
            );
        }
        return loginVK(username);
    }

    private ResponseEntity<String> loginVK(String username) {
        User user = userService.findByUsername(username);
        if (user != null && user.getVk()) {
            String token = jwtProvider.generateToken(username);
            return responseSuccess("response", token);
        } else
            return responseBad("response", "User not found");
    }

}
