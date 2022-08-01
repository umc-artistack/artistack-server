package com.artistack.controller;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;


import com.artistack.base.constant.Code;
import com.artistack.upload.dto.UploadDto;
import java.io.FileInputStream;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;


@DisplayName("Controller - Upload")
class UploadControllerTest extends BaseControllerTest {

    @BeforeEach
    void setUp() {
    }


    @Test
    @DisplayName("단일 파일 업로드")
    void uploadFileTest() throws Exception {
        uploadFile("/profile", Code.OK.getCode());
    }

    String uploadFile(String path, int code) throws Exception {
        MockMultipartFile image = new MockMultipartFile("file", "test.png", "image/png",
            new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/test.png"));

        MvcResult res = mockMvc.perform(RestDocumentationRequestBuilders.multipart("/upload" + path + "?multiple=false")
                .file(image)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            )
            .andExpect(jsonPath("$.code").value(code))
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return map.get("data").toString();
    }

    @Test
    @DisplayName("복수 파일 업로드")
    void uploadFilesTest() throws Exception {
        UploadDto response = uploadFiles("/profile", Code.OK.getCode());
        then(response.getUrls().size()).isEqualTo(3);
    }

    UploadDto uploadFiles(String path, int code) throws Exception {
        MockMultipartFile image = new MockMultipartFile("files", "test.png", "image/png",
            new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/test.png"));

        MvcResult res = mockMvc.perform(RestDocumentationRequestBuilders.multipart("/upload" + path + "?multiple=true")
                .file(image)
                .file(image)
                .file(image)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            )
            .andExpect(jsonPath("$.code").value(code))
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("data")), UploadDto.class);
    }
}

