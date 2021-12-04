package com.example.workingwithtokens;


import com.example.workingwithtokens.providers.JwtProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class VkAuthTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtProvider provider;

    @Autowired
    private Environment environment;

    @Test
    public void redirectVk() throws Exception {
        final String clientId = environment.getProperty("vk.client-id");
        this.mockMvc.perform(post("https://localhost:8080/vk/auth"))
                .andExpect(redirectedUrl("https://oauth.vk.com/authorize" +
                        "?client_id=" + clientId +
                        "&scope=email" +
                        "&v=5.59" +
                        "&redirect_uri=https://driver-diary.xyz:8080/vk/accessing"));
    }
}
