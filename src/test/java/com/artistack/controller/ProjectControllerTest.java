package com.artistack.controller;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.artistack.base.GeneralException;
import com.artistack.base.constant.Code;
import com.artistack.instrument.dto.InstrumentDto;
import com.artistack.jwt.dto.JwtDto;

import com.artistack.instrument.domain.Instrument;
import com.artistack.instrument.domain.ProjectInstrument;
import com.artistack.instrument.repository.InstrumentRepository;
import com.artistack.instrument.repository.ProjectInstrumentRepository;
import com.artistack.project.constant.Scope;
import com.artistack.jwt.dto.JwtDto;
import com.artistack.project.domain.Project;

import com.artistack.instrument.dto.InstrumentDto;

import com.artistack.project.dto.ProjectDto;
import com.artistack.project.repository.ProjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
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

    @Autowired
    private ProjectInstrumentRepository projectInstrumentRepository;

    @Autowired
    private InstrumentRepository instrumentRepository;

    OAuthControllerTest oAuthControllerTest = new OAuthControllerTest();
    HashMap<String, Object> registerBody;
    String accessToken;
    List<Long> instrumentIds = List.of(1L);

    @BeforeEach
    void setUp() {
        oAuthControllerTest.mockMvc = mockMvc;

        registerBody = new HashMap<>() {{
            put("artistackId", "hahaha");
            put("nickname", "hahaha");
            put("description", "테스트입니다");
            put("profileImgUrl", "www.naver.com");
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
        // 프로젝트 등록 전 프로젝트 수를 count
        long beforeUpload = projectRepository.count();

        // 프로젝트 등록
        uploadProject(accessToken, 0, Scope.PUBLIC, true, Code.OK.getCode());

        // 프로젝트 등록 후 프로젝트 수를 count
        long afterUpload = projectRepository.count();
        then(afterUpload).isEqualTo(beforeUpload + 1);

        long projectId = projectRepository.findTopByOrderByIdDesc().getId();

        // 검증 단계에서 사용할 Entity
        Project projectEntity = projectRepository.findById(projectId).orElse(null);

        // 요청값으로부터 만들어지는 악기 리스트
        List<Instrument> getInstrumentsFromEntity = new ArrayList<>();
        for (Long instrumentId : instrumentIds) {
            Instrument instrumentEntity = instrumentRepository.findById(instrumentId)
                .orElseThrow(() -> new GeneralException(Code.INVALID_INSTRUMENT, "올바른 악기를 선택해주세요."));

            ProjectInstrument projectInstrumentEntity = new ProjectInstrument(projectEntity, instrumentEntity);

            getInstrumentsFromEntity.add(projectInstrumentEntity.getInstrument());

        }
        then(getInstrumentsFromEntity.size()).isEqualTo(1);

        // Repository에 저장된 악기 리스트
        List<Instrument> getInstrumentsFromRepository = new ArrayList<>();
        for (ProjectInstrument projectInstrument : projectInstrumentRepository.findByProjectId(projectId)) {
            getInstrumentsFromRepository.add(projectInstrument.getInstrument());
        }

        // 요청 값과 저장 값이 같은지 검증
        then(projectEntity).isNotNull();
        then(projectEntity.getPrevProjectId()).isEqualTo(0L);
        then(projectEntity.getTitle()).isEqualTo("프로젝트 제목입니다");
        then(projectEntity.getDescription()).isEqualTo("프로젝트 설명입니다");
        then(projectEntity.getCodeFlow()).isEqualTo("A B C D");
        then(projectEntity.getBpm()).isEqualTo("123");
        then(getInstrumentsFromEntity).isEqualTo(getInstrumentsFromRepository);
        then(projectEntity.getScope()).isEqualTo(Scope.PUBLIC);
        then(projectEntity.getIsStackable()).isEqualTo(true);
    }

    @Test
    @DisplayName("등록 - 스택 쌓기")
    void uploadStackTest() throws Exception {
        // 스택을 쌓을 이전 프로젝트를 먼저 등록
        uploadProject(accessToken, 0, Scope.PUBLIC, true, Code.OK.getCode());

        // 스택을 쌓기 전 프로젝트 수
        long beforeStackCnt = projectRepository.count();

        // 이전 프로젝트
        Long prevProjectId = projectRepository.findTopByOrderByIdDesc().getId();
        // 스택을 쌓을 새로운 유저
        JwtDto jwt = oAuthControllerTest.signUp(oAuthControllerTest.testUserRegisterBody, Code.OK.getCode());

        // 이전 프로젝트 위에 스택을 쌓음
        uploadProject(jwt.getAccessToken(), prevProjectId.intValue(), Scope.PUBLIC, true, Code.OK.getCode());

        // 스택을 쌓은 후 프로젝트 수
        long afterStackCnt = projectRepository.count();
        then(afterStackCnt).isEqualTo(beforeStackCnt + 1);

        // 검증 단계에서 사용할 Entity
        Project project = projectRepository.findTopByOrderByIdDesc();

        // 검증
        then(project.getPrevProjectId()).isEqualTo(prevProjectId);
    }

    // 프로젝트 업로드
    String uploadProject(String accessToken, Integer prev, Scope scope, Boolean isStackable, int code) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MockMultipartFile video = new MockMultipartFile("video", "test.mp4", "video/mpeg",
            new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/test.mp4"));

        ProjectDto projectDto = ProjectDto.insertProject(
            "프로젝트 제목입니다",
            "프로젝트 설명입니다",
            "123",
            "A B C D",
            instrumentIds,
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

    // 메이슨
    @Test
    @DisplayName("프로젝트들 조회")
    void getProjectsTest() throws Exception {

        int projectCnt = 5, pageSize = 20, otherUserProjectCnt = 2;
        List<String> uploadUrls = new ArrayList<>();
        for (int i = 0; i < projectCnt; i++) {
            uploadUrls.add(uploadProject(accessToken, 0, Scope.PUBLIC, true, Code.OK.getCode()));
        }

        JwtDto jwt = oAuthControllerTest.signUp(oAuthControllerTest.testUserRegisterBody, Code.OK.getCode());
        for (int i = 0; i < otherUserProjectCnt; i++) {
            uploadUrls.add(uploadProject(jwt.getAccessToken(), 0, Scope.PUBLIC, true, Code.OK.getCode()));
        }
        List<ProjectDto> res = getProjects(accessToken, pageSize, Code.OK.getCode());
        Collections.reverse(res);

        for (int i = 0; i < uploadUrls.size(); i++) {
            then(res.get(i).getVideoUrl()).isEqualTo(uploadUrls.get(i));
        }
        
        Collections.reverse(res);
        int lastIdx = 3;
        List<ProjectDto> lastIdRes = getProjectsByLastId(accessToken, res.get(lastIdx).getId().intValue(), pageSize, Code.OK.getCode());
        then(lastIdRes.get(0).getId()).isEqualTo(res.get(lastIdx + 1).getId());
    }

    // 메이슨
    List<ProjectDto> getProjects(String ac, int pageSize, int code)
        throws Exception {
        MvcResult res = mockMvc.perform(
                get(String.format("/projects/search?page=0&size=%d&sort=id,desc", pageSize))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + ac)
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        map = gson.fromJson(gson.toJsonTree(map.get("data")), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("content")), new TypeToken<ArrayList<ProjectDto>>() {
        }.getType());
    }

    // 메이슨
    List<ProjectDto> getProjectsByLastId(String ac, int lastId, int pageSize, int code)
        throws Exception {
        MvcResult res = mockMvc.perform(
                get(String.format("/projects/search?page=0&size=%d&sort=id,desc&lastId=%d", pageSize, lastId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + ac)
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        map = gson.fromJson(gson.toJsonTree(map.get("data")), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("content")), new TypeToken<ArrayList<ProjectDto>>() {
        }.getType());
    }

    // 메이슨
    @Test
    @DisplayName("artistack id로 프로젝트들 조회")
    void getProjectsByArtistackIdTest() throws Exception {

        int projectCnt = 5, pageSize = 20, otherUserProjectCnt = 2;
        List<String> uploadUrls = new ArrayList<>();
        for (int i = 0; i < projectCnt; i++) {
            uploadUrls.add(uploadProject(accessToken, 0, Scope.PUBLIC, true, Code.OK.getCode()));
        }

        JwtDto jwt = oAuthControllerTest.signUp(oAuthControllerTest.testUserRegisterBody, Code.OK.getCode());
        for (int i = 0; i < otherUserProjectCnt; i++) {
            uploadProject(jwt.getAccessToken(), 0, Scope.PUBLIC, true, Code.OK.getCode());
        }

        List<ProjectDto> res = getProjectsByArtistackId(accessToken,
            registerBody.get("artistackId").toString(), pageSize, Code.OK.getCode());

        for (int i = 0; i < res.size(); i++) {
            then(res.get(i).getVideoUrl()).isEqualTo(uploadUrls.get(i));
        }

        then(res.size()).isEqualTo(Math.min(projectCnt, pageSize));
        then(projectRepository.countPublicByArtistackId(registerBody.get("artistackId").toString())).isEqualTo(
            projectCnt);
        then(projectRepository.countPublicByArtistackId(
            oAuthControllerTest.testUserRegisterBody.get("artistackId").toString())).isEqualTo(otherUserProjectCnt);
    }

    // 메이슨
    List<ProjectDto> getProjectsByArtistackId(String ac, String artistackId, int pageSize, int code)
        throws Exception {
        MvcResult res = mockMvc.perform(
                get(String.format("/projects/search?artistackId=%s&page=0&size=%d", artistackId, pageSize))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + ac)
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        map = gson.fromJson(gson.toJsonTree(map.get("data")), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("content")), new TypeToken<ArrayList<ProjectDto>>() {
        }.getType());
    }

    // 메이슨
    @Test
    @DisplayName("내 프로젝트들 조회")
    void getMyProjectsTest() throws Exception {

        int projectCnt = 5, pageSize = 3;
        List<String> uploadUrls = new ArrayList<>();
        for (int i = 0; i < projectCnt; i++) {
            uploadUrls.add(uploadProject(accessToken, 0, Scope.PUBLIC, true, Code.OK.getCode()));
        }
        uploadProject(accessToken, 0, Scope.PRIVATE, true, Code.OK.getCode());

        List<ProjectDto> res = getMyProjects(accessToken, pageSize, Code.OK.getCode());

        for (int i = 0; i < res.size(); i++) {
            then(res.get(i).getVideoUrl()).isEqualTo(uploadUrls.get(i));
        }

        then(res.size()).isEqualTo(Math.min(projectCnt, pageSize));
        then(projectRepository.countPublicByArtistackId(registerBody.get("artistackId").toString())).isEqualTo(
            projectCnt);
    }

    // 메이슨
    List<ProjectDto> getMyProjects(String ac, int pageSize, int code)
        throws Exception {
        MvcResult res = mockMvc.perform(
                get(String.format("/projects/me?page=0&size=%d", pageSize))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + ac)
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        map = gson.fromJson(gson.toJsonTree(map.get("data")), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("content")), new TypeToken<ArrayList<ProjectDto>>() {
        }.getType());
    }

    // 메이슨
    @Test
    @DisplayName("내 프로젝트 삭제")
    void deleteMyProjectTest() throws Exception {
        int projectCnt = 5, pageSize = 3;
        List<String> uploadUrls = new ArrayList<>();
        for (int i = 0; i < projectCnt; i++) {
            uploadUrls.add(uploadProject(accessToken, 0, Scope.PUBLIC, true, Code.OK.getCode()));
        }

        then(projectRepository.countPublicByArtistackId(registerBody.get("artistackId").toString())).isEqualTo(
            projectCnt);

        List<ProjectDto> res = getMyProjects(accessToken, pageSize, Code.OK.getCode());

        Long targetId = res.get(0).getId();

        deleteMyProject(accessToken, targetId.intValue(), Code.OK.getCode());

        then(projectRepository.countPublicByArtistackId(registerBody.get("artistackId").toString())).isEqualTo(
            projectCnt - 1);
    }

    // 메이슨
    void deleteMyProject(String ac, int projectId, int code)
        throws Exception {
        MvcResult res = mockMvc.perform(
                delete("/projects/" + projectId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + ac)
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andReturn();
    }


}
