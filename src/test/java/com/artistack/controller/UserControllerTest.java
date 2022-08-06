package com.artistack.controller;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.artistack.base.constant.Code;
import com.artistack.instrument.repository.UserInstrumentRepository;
import com.artistack.jwt.dto.JwtDto;
import com.artistack.oauth.repository.KakaoAccountRepository;
import com.artistack.user.constant.Role;
import com.artistack.user.domain.User;
import com.artistack.user.dto.UserDto;
import com.artistack.user.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;


@DisplayName("Controller - User")
class UserControllerTest extends BaseControllerTest {

    @Autowired
    private KakaoAccountRepository kakaoAccountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserInstrumentRepository userInstrumentRepository;

    OAuthControllerTest oAuthControllerTest = new OAuthControllerTest();

    @BeforeEach
    void setUp() {
        oAuthControllerTest.mockMvc = mockMvc;
    }

    @Test
    @DisplayName("내 정보 조회")
    void getMeTest() throws Exception {
        JwtDto jwt = oAuthControllerTest.signUp(oAuthControllerTest.testUserRegisterBody, Code.OK.getCode());

        UserDto res = getMe(jwt.getAccessToken(), Code.OK.getCode());

        then(res.getDescription()).isEqualTo(oAuthControllerTest.testUserRegisterBody.get("description"));
        then(res.getNickname()).isEqualTo(oAuthControllerTest.testUserRegisterBody.get("nickname"));
        then(res.getArtistackId()).isEqualTo(oAuthControllerTest.testUserRegisterBody.get("artistackId"));
        then(res.getProfileImgUrl()).isEqualTo(oAuthControllerTest.testUserRegisterBody.get("profileImgUrl"));
    }

    UserDto getMe(String ac, int code) throws Exception {
        MvcResult res = mockMvc.perform(get("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + ac)
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("data")), UserDto.class);
    }

    @Test
    @DisplayName("다른 유저 정보 조회")
    void getUserTest() throws Exception {
        JwtDto jwt = oAuthControllerTest.signUp(oAuthControllerTest.testUserRegisterBody, Code.OK.getCode());
        String anotherUserArtistackId = "anotheruser";
        oAuthControllerTest.testUserRegisterBody.put("artistackId", anotherUserArtistackId);
        oAuthControllerTest.testUserRegisterBody.put("nickname", "다른가입유저");
        oAuthControllerTest.signUp(oAuthControllerTest.testUserRegisterBody, Code.OK.getCode());

        UserDto res = getUser(jwt.getAccessToken(), anotherUserArtistackId, Code.OK.getCode());

        then(res.getDescription()).isEqualTo(oAuthControllerTest.testUserRegisterBody.get("description"));
        then(res.getNickname()).isEqualTo(oAuthControllerTest.testUserRegisterBody.get("nickname"));
        then(res.getArtistackId()).isEqualTo(oAuthControllerTest.testUserRegisterBody.get("artistackId"));
        then(res.getProfileImgUrl()).isEqualTo(oAuthControllerTest.testUserRegisterBody.get("profileImgUrl"));
    }

    UserDto getUser(String ac, String artistackId, int code) throws Exception {
        MvcResult res = mockMvc.perform(get("/users/"+artistackId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + ac)
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("data")), UserDto.class);
    }

    @Test
    @DisplayName("회원 정보 수정1")
    void updateUserTest1() throws Exception {
        JwtDto jwt = oAuthControllerTest.signUp(oAuthControllerTest.testUserRegisterBody, Code.OK.getCode());

        HashMap<String, Object> body = new HashMap<>() {{
            put("description", "세로운 한줄 설명");
            put("nickname", "newnewnick");
        }};

        UserDto res = updateUser(jwt.getAccessToken(), body, Code.OK.getCode());

        then(res.getDescription()).isEqualTo(body.get("description"));
        then(res.getNickname()).isEqualTo(body.get("nickname"));
        then(res.getArtistackId()).isEqualTo(oAuthControllerTest.testUserRegisterBody.get("artistackId"));
        then(res.getProfileImgUrl()).isEqualTo(oAuthControllerTest.testUserRegisterBody.get("profileImgUrl"));
    }

    @Test
    @DisplayName("회원 정보 수정2")
    void updateUserTest2() throws Exception {
        JwtDto jwt = oAuthControllerTest.signUp(oAuthControllerTest.testUserRegisterBody, Code.OK.getCode());

        HashMap<String, Object> body = new HashMap<>() {{
            put("profileImgUrl", "https://newnew....");
        }};

        UserDto res = updateUser(jwt.getAccessToken(), body, Code.OK.getCode());

        then(res.getDescription()).isEqualTo(oAuthControllerTest.testUserRegisterBody.get("description"));
        then(res.getNickname()).isEqualTo(oAuthControllerTest.testUserRegisterBody.get("nickname"));
        then(res.getArtistackId()).isEqualTo(oAuthControllerTest.testUserRegisterBody.get("artistackId"));
        then(res.getProfileImgUrl()).isEqualTo(body.get("profileImgUrl"));
    }

    @Test
    @DisplayName("회원 정보 수정 실패 (닉네임 공백)")
    void updateUserFailTest1() throws Exception {
        JwtDto jwt = oAuthControllerTest.signUp(oAuthControllerTest.testUserRegisterBody, Code.OK.getCode());

        HashMap<String, Object> body = new HashMap<>() {{
            put("nickname", "newnew nick");
            put("description", "fpgksdfkd");

        }};

        updateUser(jwt.getAccessToken(), body, Code.NICKNAME_FORMAT_ERROR.getCode());
    }

    @Test
    @DisplayName("회원 정보 수정 실패 (설명 길이)")
    void updateUserFailTest2() throws Exception {
        JwtDto jwt = oAuthControllerTest.signUp(oAuthControllerTest.testUserRegisterBody, Code.OK.getCode());

        HashMap<String, Object> body = new HashMap<>() {{
            put("description", "newdfasdfasdjbfhjagiajgooijwgjafsdnew nick");
        }};

        updateUser(jwt.getAccessToken(), body, Code.USER_DESCRIPTION_FORMAT_ERROR.getCode());
    }

    UserDto updateUser(String ac, HashMap<String, Object> body, int code) throws Exception {
        MvcResult res = mockMvc.perform(patch("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + ac)
                .content(objectMapper.writeValueAsString(body))
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("data")), UserDto.class);
    }


    @Test
    @DisplayName("닉네임 중복 체크")
    void checkArtistackIdDuplicateTest() throws Exception {
        String duplicateId = "dasdfasf";
        oAuthControllerTest.testUserRegisterBody.put("artistackId", duplicateId);
        JwtDto jwt = oAuthControllerTest.signUp(oAuthControllerTest.testUserRegisterBody, Code.OK.getCode());

        Boolean res = checkArtistackIdDuplicate(jwt.getAccessToken(), duplicateId, Code.OK.getCode());
        then(res).isTrue();

        res = checkArtistackIdDuplicate(jwt.getAccessToken(), "aaaaaaa", Code.OK.getCode());
        then(res).isFalse();
    }

    Boolean checkArtistackIdDuplicate(String ac, String value, int code) throws Exception {
        MvcResult res = mockMvc.perform(get("/users/duplicate?type=artistackId&value=" + value)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + ac)
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return Boolean.parseBoolean(map.get("data").toString());
    }

    @Test
    @DisplayName("회원 탈퇴")
    void withdrawUserTest() throws Exception {
        JwtDto jwt = oAuthControllerTest.signUp(oAuthControllerTest.testUserRegisterBody, Code.OK.getCode());
        Long userId = userRepository.findByArtistackId(
            (String) oAuthControllerTest.testUserRegisterBody.get("artistackId")).get().getId();

        Boolean res = withdrawUser(jwt.getAccessToken(), Code.OK.getCode());
        then(res).isTrue();

        User user = userRepository.findById(userId).get();
        then(user.getRole()).isEqualTo(Role.WITHDRAWAL);
        then(user.getArtistackId()).isNull();
        then(user.getNickname()).isNull();
        then(user.getDescription()).isNull();
        then(user.getProfileImgUrl()).isNull();
    }

    Boolean withdrawUser(String ac, int code) throws Exception {
        MvcResult res = mockMvc.perform(delete("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + ac)
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return Boolean.parseBoolean(map.get("data").toString());
    }

    @Test
    @EnabledIf("iskakaoTokenPresent")
    @DisplayName("카카오 회원 탈퇴")
    void withdrawKakaoUserTest() throws Exception {
        oAuthControllerTest.testUserRegisterBody.put("providerType", "KAKAO");
        JwtDto jwt = oAuthControllerTest.signUp(oAuthControllerTest.testUserRegisterBody, Code.OK.getCode());
        Long userId = userRepository.findByArtistackId(
            (String) oAuthControllerTest.testUserRegisterBody.get("artistackId")).get().getId();

        then(kakaoAccountRepository.findByUserId(userId)).isPresent();

        Boolean res = withdrawUser(jwt.getAccessToken(), Code.OK.getCode());
        then(res).isTrue();

        then(kakaoAccountRepository.findByUserId(userId)).isEmpty();

        User user = userRepository.findById(userId).get();
        then(user.getRole()).isEqualTo(Role.WITHDRAWAL);
        then(user.getArtistackId()).isNull();
        then(user.getNickname()).isNull();
        then(user.getDescription()).isNull();
        then(user.getProfileImgUrl()).isNull();
    }

    boolean iskakaoTokenPresent() {
        return !oAuthControllerTest.kakaoToken.isBlank();
    }
}

