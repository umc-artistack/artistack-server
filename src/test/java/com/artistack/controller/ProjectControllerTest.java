package com.artistack.controller;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.artistack.base.constant.Code;
import com.artistack.instrument.dto.InstrumentDto;
import com.artistack.project.dto.ProjectDto;
import com.artistack.project.repository.ProjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MvcResult;

@DisplayName("Controller - Project")
class ProjectControllerTest extends BaseControllerTest {

    @Autowired
    private ProjectRepository projectRepository;
    OAuthControllerTest oAuthControllerTest = new OAuthControllerTest();
    HashMap<String, Object> registerBody;
    String accessToken;
    List<Long> instrumentIds = List.of(1L, 3L);

    @BeforeEach
    void setUp() {
        oAuthControllerTest.mockMvc = mockMvc;

        registerBody = new HashMap<>() {{
            put("artistackId", "hahaha");
            put("nickname", "hahaha");
            put("description", "테스트입니다");
            put("providerType", "TEST");
            put("instruments", new ArrayList<>() {{
                instrumentIds.forEach(id ->
                    add(new HashMap<>() {{
                        put("id", id);
                    }})
                );
            }});
        }};

        try {
            accessToken = oAuthControllerTest.signUp(registerBody, Code.OK.getCode()).getAccessToken();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @AfterEach
    void cleanUp() {
        projectRepository.deleteAll();
    }

    @Test
    @DisplayName("등록 - 최초")
    void uploadInitialTest() throws Exception {
        uploadProject(accessToken, 0, 1, true, Code.OK.getCode());
    }

    @Test
    @DisplayName("등록 - 다른 프로젝트 위에")
    void uploadTest() throws Exception {
        uploadInitialTest();
        Integer projectCnt = Long.valueOf(projectRepository.count()).intValue();
        uploadProject(accessToken, projectCnt, 1, true, Code.OK.getCode());
    }

    /**
     * TODO: 이전 프로젝트가 없는데 쌓을 경우
     * TODO: 이전 프로젝트가 스택 비허용인데 쌓을 경우
     */

//    @Test
//    @DisplayName("스택 실패 - 이전 프로젝트 없음")
//    void stackFailWithoutPrev() throws Exception {
//        uploadProject(accessToken, 99, 1, true, Code.PREV_PROJECT_NOT_EXIST.getCode());
//    }

    String uploadProject(String accessToken, Integer prev, Integer scope, Boolean isStackable, int code)
        throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MockMultipartFile video = new MockMultipartFile("video", "test.mp4", "video/mpeg",
            new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/test.mp4"));

        List<Long> instrumentIds = List.of(1L, 3L);

        List<InstrumentDto> instruments = new ArrayList<>();
        for (Long id : instrumentIds) {
            instruments.add(new InstrumentDto(id, null, null));
        }

        ProjectDto projectDto = ProjectDto.insertProject(
            "프로젝트 제목입니다",
            "프로젝트 설명입니다",
            "123",
            "A B C D",
            instruments,
            scope,
            isStackable
        );

        String projectDtoJson = mapper.writeValueAsString(projectDto);
        MockMultipartFile dto = new MockMultipartFile("dto", "dto", "application/json", projectDtoJson.getBytes(
            StandardCharsets.UTF_8));

        MvcResult res = mockMvc.perform(RestDocumentationRequestBuilders.multipart("/projects/" + prev)
                .file(video)
                .file(dto)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            .andExpect(jsonPath("$.code").value(code))
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);

        return map.get("data").toString();
    }
}
