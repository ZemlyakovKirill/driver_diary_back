package com.example.workingwithtokens;


import com.example.workingwithtokens.controllers.AuthController;
import com.example.workingwithtokens.providers.JwtProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class StandardRegistrationTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtProvider provider;

    @Autowired
    AuthController authController;

    //    {
//    "username":"ivan",
//    "password":"1234",
//    "lname":"Ivanov",
//    "fname":"Ivan",
//    "email":"ivanov@ivan.ru",
//    }
    @Test
    public void registrationWithoutPhone() throws Exception {
        this.mockMvc.perform(post("https://localhost:8080/auth/registrate")
                        .param("username", "ivanov")
                        .param("password", "12345")
                        .param("lname", "Иванов")
                        .param("email", "ivano@ivan.ru")
                        .param("fname", "Иван"))
                .andExpect(status().is(201))
                .andExpect(content().json("{\n\"status\":201\n}"));
    }

    @Test
    public void registrationWithPhone() throws Exception {
        this.mockMvc.perform(post("https://localhost:8080/auth/registrate")
                        .param("username", "ivano")
                        .param("password", "12345")
                        .param("lname", "Иванов")
                        .param("email", "ivanov@ivan.ru")
                        .param("fname", "Иван")
                        .param("telnum", "79992654055"))
                .andExpect(status().is(201))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void registrationWithWrongParams() throws Exception {
        this.mockMvc.perform(post("https://localhost:8080/auth/registrate")
                        .param("username", "i")
                        .param("password", "1")
                        .param("lname", "И")
                        .param("email", "i")
                        .param("fname", "И")
                        .param("phone", "7"))
                .andExpect(status().is(400))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void registrateViaGet() throws Exception{
        this.mockMvc.perform(get("https://localhost:8080/auth/registrate"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().json("{\n" +
                        "  \"response\": \"Request method \\u0027GET\\u0027 not supported\",\n" +
                        "  \"status\": 400\n" +
                        "}"));
    }

}
