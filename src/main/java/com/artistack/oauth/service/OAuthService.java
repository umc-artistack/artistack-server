package com.artistack.oauth.service;


import com.artistack.base.GeneralException;
import com.artistack.base.constant.Code;
import com.artistack.instrument.service.InstrumentService;
import com.artistack.jwt.dto.JwtDto;
import com.artistack.jwt.service.JwtService;
import com.artistack.oauth.constant.ProviderType;
import com.artistack.oauth.domain.KakaoAccount;
import com.artistack.oauth.dto.KakaoAccountDto;
import com.artistack.oauth.repository.KakaoAccountRepository;
import com.artistack.user.domain.User;
import com.artistack.user.dto.UserDto;
import com.artistack.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OAuthService {

    private final KakaoAccountRepository kakaoAccountRepository;
    private final UserRepository userRepository;
    private final InstrumentService instrumentService;
    private final JwtService jwtService;

    @Value("${security.oauth2.provider.kakao.user-info-uri}")
    private String KAKAO_USER_INFO_URI;
    @Value("${security.oauth2.provider.kakao.unlink-uri}")
    private String KAKAO_UNLINK_URI;
    @Value("${security.oauth2.provider.kakao.admin-key}")
    private String KAKAO_ADMIN_KEY;

    /**
     * 주어진 provider type으로 회원을 식별하여 로그인을 진행합니다.
     *
     * @param providerType        KAKAO/APPLE
     * @param providerAccessToken provider측 user 식별을 위한 token
     * @return 가입된 회원이라면 access/refresh token 발급, 그렇지 않다면 provider token으로 얻은 정보를 반환합니다.
     */
    public Object signIn(ProviderType providerType, String providerAccessToken) {
        Optional<User> user = Optional.empty();

        switch (providerType) {
            case KAKAO:
                KakaoAccountDto kakaoAccountDto = getKakaoAccount(providerAccessToken);
                user = kakaoAccountRepository.findById(kakaoAccountDto.getId()).map(KakaoAccount::getUser);
                if (user.isEmpty()) {
                    return kakaoAccountDto.toUserDto();
                }
                break;
            case APPLE:
                break;
        }
        return jwtService.issueJwt(user.get());
    }


    /**
     * 회원가입을 진행합니다.
     *
     * @param userDto             회원가입 정보를 담은 body
     * @param providerAccessToken provider측 user 식별을 위한 token
     * @return access/refresh token
     */
    public JwtDto signUp(UserDto userDto, String providerAccessToken) {
        KakaoAccountDto kakaoAccountDto = null;

        // 이미 회원가입이 되어있는지 확인
        if (userDto.getProviderType().equals(ProviderType.KAKAO)) {
            kakaoAccountDto = getKakaoAccount(providerAccessToken);
            if (kakaoAccountRepository.findById(kakaoAccountDto.getId()).isPresent()) {
                throw new GeneralException(Code.ALREADY_REGISTERED, userDto.getProviderType().toString());
            }
        }

        User user = userRepository.save(userDto.toEntity(userRepository));

        // provider repository에 각기 저장
        if (userDto.getProviderType().equals(ProviderType.KAKAO)) {
            assert kakaoAccountDto != null;
            kakaoAccountRepository.save(KakaoAccount.builder().id(kakaoAccountDto.getId()).user(user).build());
        }

        instrumentService.insertByUserId(userDto.getInstruments(), user.getId());

        return jwtService.issueJwt(user);
    }


    private KakaoAccountDto getKakaoAccount(String kakaoAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", kakaoAccessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<KakaoAccountDto> response;
        try {
            response = new RestTemplate().exchange(
                KAKAO_USER_INFO_URI,
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                KakaoAccountDto.class
            );
        } catch (RestClientException e) {
            log.trace("Kakao token authorization failed", e);
            throw new GeneralException(Code.KAKAO_SERVER_ERROR, e);
        }
        return response.getBody();
    }


    public void unlinkKakaoAccount(Long userId) {
        KakaoAccount kakaoAccount = kakaoAccountRepository.findByUserId(userId)
            .orElseThrow(() -> new GeneralException(Code.USER_NOT_FOUND, "Fail to unlink kakao account"));

        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        headers.set("Authorization", "KakaoAK " + KAKAO_ADMIN_KEY);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        params.add("target_id_type", "user_id");
        params.add("target_id", kakaoAccount.getId());

        try {
            new RestTemplate().exchange(
                KAKAO_UNLINK_URI,
                HttpMethod.POST,
                new HttpEntity<>(params, headers),
                String.class
            );
        } catch (RestClientException e) {
            log.trace("Fail to unlink kakao account", e);
            throw new GeneralException(Code.KAKAO_SERVER_ERROR, "Fail to unlink kakao account", e);
        }

        kakaoAccountRepository.deleteByUserId(userId);
    }
}