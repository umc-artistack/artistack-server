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
import com.artistack.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OAuthService {

    private final KakaoAccountRepository kakaoAccountRepository;
    private final UserRepository userRepository;
    private final InstrumentService instrumentService;
    private final UserService userService;
    private final JwtService jwtService;

    @Value("${security.oauth2.provider.kakao.user-info-uri}")
    private String KAKAO_USER_INFO_URL;

    /**
     * 주어진 provider type으로 회원을 식별하여 로그인을 진행합니다.
     *
     * @param providerType        KAKAO/APPLE
     * @param providerAccessToken provider측 user 식별을 위한 token
     * @return 가입된 회원이라면 access/refresh token 발급, 그렇지 않다면 provider token으로 얻은 정보를 반환합니다.
     */
    public Object signIn(ProviderType providerType, String providerAccessToken) {
        Optional<User> user = Optional.empty();

        if (providerType.equals(ProviderType.KAKAO)) {
            KakaoAccountDto kakaoAccountDto = getKakaoAccount(providerAccessToken);
            user = kakaoAccountRepository.findById(kakaoAccountDto.getId()).map(KakaoAccount::getUser);
            if (user.isEmpty()) {
                return kakaoAccountDto;
            }
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

        userService.validateRequest(userDto);
        User user = userRepository.save(userDto.toEntity());

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
                KAKAO_USER_INFO_URL,
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
}