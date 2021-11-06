package com.example.workingwithtokens;


import com.example.workingwithtokens.controllers.AuthController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class StandardRegistrationTests {
    @Autowired
    MockMvc mockMvc;

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
        this.mockMvc.perform(get("https://localhost:8080/auth/registrate")
                        .param("username","ivanov")
                        .param("password","12345")
                        .param("lname","Иванов")
                        .param("email","ivano@ivan.ru")
                        .param("fname","Иван"))
                .andDo(System.out::println)
                .andExpect(status().is(201)).andDo(System.out::println);
    }

    @Test
    public void registrationWithPhone() throws Exception {
        this.mockMvc.perform(get("https://localhost:8080/auth/registrate")
                        .param("username","ivano")
                        .param("password","12345")
                        .param("lname","Иванов")
                        .param("email","ivanov@ivan.ru")
                        .param("fname","Иван")
                        .param("phone","79992654055"))
                .andDo(System.out::println)
                .andExpect(status().is(201)).andDo(System.out::println);
    }

    @Test
    public void registrationWithWrongParams() throws Exception {
        this.mockMvc.perform(get("https://localhost:8080/auth/registrate")
                        .param("username","i")
                        .param("password","1")
                        .param("lname","И")
                        .param("email","i")
                        .param("fname","И")
                        .param("phone","7"))
                .andExpect(status().is(400)).andDo(System.out::println);
    }

}
