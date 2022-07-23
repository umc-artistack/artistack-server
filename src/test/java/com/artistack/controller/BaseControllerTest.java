package com.artistack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import java.util.HashMap;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;


@AutoConfigureMockMvc
@Transactional
@SpringBootTest
class BaseControllerTest {

    @Autowired
    public MockMvc mockMvc;

    HashMap<String, Object> body = new HashMap<>();
    ObjectMapper objectMapper = new ObjectMapper();
    Gson gson = new Gson();

    @BeforeEach
    void cleanUp() throws Exception {
        body = new HashMap<>();
        objectMapper = new ObjectMapper();
    }
}