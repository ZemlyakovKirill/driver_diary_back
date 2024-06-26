package ru.themlyakov.driverdiary;

import ru.themlyakov.driverdiary.providers.JwtProvider;
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
public class GoogleAuthTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtProvider provider;

    @Autowired
    private Environment environment;

    @Test
    public void redirectGoogle() throws Exception {
        final String clientId = environment.getProperty("google.client-id");
        this.mockMvc.perform(post("https://localhost:8080/google/auth"))
                .andExpect(redirectedUrl("https://accounts.google.com/o/oauth2/v2/auth" +
                        "?client_id=1086042784691-ea85h83k1a4p2c9gmoid47ba3iuntcna.apps.googleusercontent.com" +
                        "&redirect_uri=https://driver-diary.xyz:8080/google/accessing" +
                        "&response_type=code" +
                        "&scope=https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email"));
    }
}
