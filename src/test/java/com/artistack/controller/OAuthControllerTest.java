package com.artistack.controller;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.artistack.base.constant.Code;
import com.artistack.instrument.domain.Instrument;
import com.artistack.instrument.domain.UserInstrument;
import com.artistack.instrument.repository.UserInstrumentRepository;
import com.artistack.jwt.dto.JwtDto;
import com.artistack.oauth.repository.KakaoAccountRepository;
import com.artistack.user.domain.User;
import com.artistack.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;


@DisplayName("Controller - OAuth")
class OAuthControllerTest extends BaseControllerTest {

    @Autowired
    private KakaoAccountRepository kakaoAccountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserInstrumentRepository userInstrumentRepository;

    String kakaoToken = "",
        appleToken = "";


    HashMap<String, Object> registerBody;
    List<Long> instrumentIds = List.of(1L, 3L);

    @BeforeEach
    void setUp() {
        registerBody = new HashMap<>() {{
            put("artistackId", "testIdID");
            put("nickname", "nnnnocknmae");
            put("description", "안녕핫요 안녕하세요오오오옹 !!?.dwekd123 zzz");
            put("instruments", new ArrayList<>() {{
                instrumentIds.forEach(id ->
                    add(new HashMap<>() {{
                        put("id", id);
                    }})
                );
            }});
        }};
    }


    @Test
    @DisplayName("테스트 회원가입")
    void signUpTest() throws Exception {
        UploadControllerTest uploadControllerTest = new UploadControllerTest();
        uploadControllerTest.mockMvc = mockMvc;
        String profileImgUrl = uploadControllerTest.uploadFile("/profile", Code.OK.getCode());

        long userCnt = userRepository.count();
        long userInstrumentCnt = userInstrumentRepository.count();

        registerBody.put("profileImgUrl", profileImgUrl);
        registerBody.put("providerType", "TEST");
        signUp(registerBody, Code.OK.getCode());

        then(userRepository.count()).isEqualTo(userCnt + 1);
        then(userInstrumentRepository.count()).isEqualTo(userInstrumentCnt + 2);

        User user = userRepository.findByArtistackId(registerBody.get("artistackId").toString()).orElse(null);
        then(user).isNotNull();
        then(user.getNickname()).isEqualTo(registerBody.get("nickname").toString());
        then(user.getArtistackId()).isEqualTo(registerBody.get("artistackId").toString());
        then(user.getDescription()).isEqualTo(registerBody.get("description").toString());
        then(user.getProfileImgUrl()).isEqualTo(registerBody.get("profileImgUrl").toString());
        then(user.getProviderType().toString()).isEqualTo(registerBody.get("providerType").toString());

        List<UserInstrument> userInstrumentList = userInstrumentRepository.findByUserId(user.getId());
        then(userInstrumentList.stream().map(UserInstrument::getInstrument).map(Instrument::getId)).containsAll(
            instrumentIds);
    }

    JwtDto signUp(HashMap<String, Object> body, int code) throws Exception {
        MvcResult res = mockMvc.perform(get("/oauth/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + (body.get("providerType").equals("KAKAO") ? kakaoToken : "dddd"))
                .content(objectMapper.writeValueAsString(body))
            )
            .andExpect(jsonPath("$.code").value(code))
            .andReturn();
        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("data")), JwtDto.class);
    }


    @EnabledIf("iskakaoTokenPresent")
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

    @EnabledIf("iskakaoTokenPresent")
    @Test
    @DisplayName("카카오로 회원가입")
    void signUpWithKakaoTest() throws Exception {
        long kakaoCnt = kakaoAccountRepository.count();
        long userCnt = userRepository.count();

        registerBody.put("providerType", "KAKAO");

        signUp(registerBody, Code.OK.getCode());

        then(userCnt + 1).isEqualTo(userRepository.count());
        then(kakaoCnt + 1).isEqualTo(kakaoAccountRepository.count());
    }


    @EnabledIf("iskakaoTokenPresent")
    @Test
    @DisplayName("가입 한 상태에서 카카오로 로그인")
    void signInWithKakaoSuccessTest() throws Exception {
        signUpWithKakaoTest();
        signInWithKakao(Code.OK.getCode());
    }


    @EnabledIf("iskakaoTokenPresent")
    @Test
    @DisplayName("카카오 중복가입")
    void signUpWithKakaoDuplicateTest() throws Exception {
        registerBody.put("providerType", "KAKAO");
        signUp(registerBody, Code.OK.getCode());
        signUp(registerBody, Code.ALREADY_REGISTERED.getCode());
    }

    @Test
    @DisplayName("회원가입 실패(아티스택id 중복)")
    void signUpFailArtistackIdDuplicateTest() throws Exception {
        registerBody.put("providerType", "TEST");
        signUp(registerBody, Code.OK.getCode());

        body = new HashMap<>() {{
            put("artistackId", registerBody.get("artistackId"));
            put("nickname", "nnnickname");
            put("description", "descriptiondescriptiondescription");
            put("providerType", "TEST");
        }};
        signUp(body, Code.ARTISTACK_ID_DUPLICATED.getCode());
    }

    @Test
    @DisplayName("회원가입 실패(아티스택id 길이)")
    void signUpFailArtistackIdLengthTest() throws Exception {
        registerBody.put("providerType", "TEST");
        registerBody.put("artistackId", "testIddidfsafsadfkasdfsaddidi");
        signUp(registerBody, Code.ARTISTACK_ID_FORMAT_ERROR.getCode());
    }

    boolean iskakaoTokenPresent() {
        return !kakaoToken.isBlank();
    }
}

