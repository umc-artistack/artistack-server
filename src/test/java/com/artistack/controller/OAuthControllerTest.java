package com.artistack.controller;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.artistack.base.constant.Code;
import com.artistack.jwt.dto.JwtDto;
import com.artistack.oauth.repository.KakaoAccountRepository;
import com.artistack.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;


@DisplayName("Controller - OAuth")
class OAuthControllerTest extends BaseControllerTest {

    @Autowired
    private KakaoAccountRepository kakaoAccountRepository;
    @Autowired
    private UserRepository userRepository;

    String kakaoToken = "",
        appleToken = "";


    @BeforeEach
    void setUp() throws Exception {
    }


    @Disabled
    @Test
    @DisplayName("가입 안한 상태에서 카카오로 로그인")
    void signInWithKakaoNotRegisteredTest() throws Exception {
        signInWithKakao(Code.NOT_REGISTERED.getCode());
    }

    void signInWithKakao(int code) throws Exception {
        mockMvc.perform(get("/oauth/signIn?providerType=KAKAO")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + kakaoToken)
            )
            .andExpect(jsonPath("$.code").value(code))
        ;
    }

    @Disabled
    @Test
    @DisplayName("카카오로 회원가입")
    void signUpWithKakaoTest() throws Exception {
        long kakaoCnt = kakaoAccountRepository.count();
        long userCnt = userRepository.count();

        HashMap<String, Object> body = new HashMap<>() {{
            put("artistackId", "testidid");
            put("nickname", "nnnickname");
            put("instruments", new ArrayList<>() {{
                add(new HashMap<>() {{
                    put("id", "1");
                }});
                add(new HashMap<>() {{
                    put("id", "3");
                }});
            }});
            put("providerType", "KAKAO");
        }};
        signUp(body, Code.OK.getCode());

        then(userCnt + 1).isEqualTo(userRepository.count());
        then(kakaoCnt + 1).isEqualTo(kakaoAccountRepository.count());
    }

    @Test
    @DisplayName("테스트 회원가입")
    void signUpTest() throws Exception {
        long userCnt = userRepository.count();

        HashMap<String, Object> body = new HashMap<>() {{
            put("artistackId", "testidid");
            put("nickname", "nnnickname");
            put("description", "descriptiondescriptiondescription");
            put("providerType", "TEST");
        }};

        signUp(body, Code.OK.getCode());

        then(userCnt + 1).isEqualTo(userRepository.count());
    }

    JwtDto signUp(HashMap<String, Object> body, int code) throws Exception {
        MvcResult res = mockMvc.perform(get("/oauth/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + (body.get("providerType").equals("KAKAO") ? kakaoToken : ""))
                .content(objectMapper.writeValueAsString(body))
            )
            .andExpect(jsonPath("$.code").value(code))
            .andReturn()
        ;
        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("data")), JwtDto.class);
    }

    @Disabled
    @Test
    @DisplayName("가입 한 상태에서 카카오로 로그인")
    void signInWithKakaoSuccessTest() throws Exception {
        signUpWithKakaoTest();
        signInWithKakao(Code.OK.getCode());
    }


    @Disabled
    @Test
    @DisplayName("카카오 중복가입")
    void signUpWithKakaoDuplicateTest() throws Exception {
        body = new HashMap<>() {{
            put("artistackId", "testidid");
            put("nickname", "nnnickname");
            put("description", "descriptiondescriptiondescription");
            put("providerType", "KAKAO");
        }};
        signUp(body, Code.OK.getCode());
        signUp(body, Code.ALREADY_REGISTERED.getCode());
    }

    @Test
    @DisplayName("회원가입 실패(아티스택id 중복)")
    void signUpFailArtistackIdDuplicateTest() throws Exception {
        String duplicatedNickname = "testidid";
        body = new HashMap<>() {{
            put("artistackId", duplicatedNickname);
            put("nickname", "nnnickname");
            put("description", "descriptiondescriptiondescription");
            put("providerType", "TEST");
        }};
        signUp(body, Code.OK.getCode());

        body = new HashMap<>() {{
            put("artistackId", duplicatedNickname);
            put("nickname", "nnnickname");
            put("description", "descriptiondescriptiondescription");
            put("providerType", "TEST");
        }};
        signUp(body, Code.ARTISTACK_ID_DUPLICATED.getCode());
    }

    @Test
    @DisplayName("회원가입 실패(아티스택id 길이)")
    void signUpFailArtistackIdLengthTest() throws Exception {
        body = new HashMap<>() {{
            put("artistackId", "testIddidfsafsadfkasdfsaddidi");
            put("nickname", "nnnickname");
            put("description", "descriptiondescriptiondescription");
            put("providerType", "TEST");
        }};
        signUp(body, Code.ARTISTACK_ID_FORMAT_ERROR.getCode());
    }
}

