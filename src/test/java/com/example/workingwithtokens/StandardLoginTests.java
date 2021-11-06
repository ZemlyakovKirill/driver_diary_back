package com.example.workingwithtokens;

import com.example.workingwithtokens.controllers.AuthController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class StandardLoginTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AuthController authController;

    @Test
    public void wrongUserData() throws Exception {
        this.mockMvc.perform(get("https://localhost:8080/auth/login")
                        .param("username", "alyosha")
                        .param("password", "1324"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void rightUserData() throws Exception {
        this.mockMvc.perform(get("https://localhost:8080/auth/login")
                        .param("username", "ivanov")
                        .param("password", "12345"))
                .andExpect(status().is2xxSuccessful());
    }
}
