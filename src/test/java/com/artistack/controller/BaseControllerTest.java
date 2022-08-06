package com.artistack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
        @Override
        public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Instant instant = Instant.ofEpochMilli(json.getAsJsonPrimitive().getAsLong());
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        }
    }).create();

    @BeforeEach
    void cleanUp() throws Exception {
        body = new HashMap<>();
        objectMapper = new ObjectMapper();
    }
}