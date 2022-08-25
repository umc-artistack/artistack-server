package com.artistack.oauth.service;


import com.artistack.base.GeneralException;
import com.artistack.base.constant.Code;
import com.artistack.instrument.service.InstrumentService;
import com.artistack.jwt.dto.JwtDto;
import com.artistack.jwt.service.JwtService;
import com.artistack.oauth.constant.ProviderType;
import com.artistack.oauth.domain.AppleAccount;
import com.artistack.oauth.domain.KakaoAccount;
import com.artistack.oauth.dto.AppleAccountDto;
import com.artistack.oauth.dto.ApplePublicKeyDto;
import com.artistack.oauth.dto.AppleTokenDto;
import com.artistack.oauth.dto.KakaoAccountDto;
import com.artistack.oauth.repository.AppleAccountRepository;
import com.artistack.oauth.repository.KakaoAccountRepository;
import com.artistack.user.domain.User;
import com.artistack.user.dto.UserDto;
import com.artistack.user.repository.UserRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
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
    private final AppleAccountRepository appleAccountRepository;
    private final UserRepository userRepository;
    private final InstrumentService instrumentService;
    private final JwtService jwtService;

    @Value("${security.oauth2.provider.kakao.user-info-uri}")
    private String KAKAO_USER_INFO_URI;
    @Value("${security.oauth2.provider.kakao.unlink-uri}")
    private String KAKAO_UNLINK_URI;
    @Value("${security.oauth2.provider.kakao.admin-key}")
    private String KAKAO_ADMIN_KEY;

    @Value("${security.oauth2.provider.apple.team-id}")
    private String appleTeamId;
    @Value("${security.oauth2.provider.apple.client-id}")
    private String appleClientId;
    @Value("${security.oauth2.provider.apple.key-id}")
    private String appleKeyId;
    @Value("${security.oauth2.provider.apple.private-key}")
    private String applePrivateKey;

    /**
     * 주어진 provider type으로 회원을 식별하여 로그인을 진행합니다.
     *
     * @param providerType        KAKAO/APPLE
     * @param providerAuthorization provider측 user 식별을 위한 token or code
     * @return 가입된 회원이라면 access/refresh token 발급, 그렇지 않다면 provider token으로 얻은 정보를 반환합니다.
     */
    public Object signIn(ProviderType providerType, String providerAuthorization) {
        Optional<User> user = Optional.empty();

        switch (providerType) {
            case KAKAO:
                KakaoAccountDto kakaoAccountDto = getKakaoAccount(providerAuthorization);
                user = kakaoAccountRepository.findById(kakaoAccountDto.getId()).map(KakaoAccount::getUser);
                if (user.isEmpty()) {
                    return kakaoAccountDto.toUserDto();
                }
                break;
            case APPLE:
                AppleTokenDto appleToken = getAppleToken(providerAuthorization);
                AppleAccountDto appleAccountDto = getAppleAccount(appleToken.getId_token());
                user = appleAccountRepository.findById(appleAccountDto.getSub()).map(AppleAccount::getUser);
                if (user.isEmpty()) {
                    return appleAccountDto.toUserDto();
                }
                break;
        }
        return jwtService.issueJwt(user.get());
    }


    /**
     * 회원가입을 진행합니다.
     *
     * @param userDto               회원가입 정보를 담은 body
     * @param providerAuthorization provider측 user 식별을 위한 token
     * @return access/refresh token
     */
    public JwtDto signUp(UserDto userDto, String providerAuthorization) {

        User user = null;

        switch (userDto.getProviderType()) {
            case KAKAO:
                KakaoAccountDto kakaoAccountDto = getKakaoAccount(providerAuthorization);
                if (kakaoAccountRepository.findById(kakaoAccountDto.getId()).isPresent()) {
                    throw new GeneralException(Code.ALREADY_REGISTERED, userDto.getProviderType().toString());
                }

                user = userRepository.save(userDto.toEntity(userRepository));
                kakaoAccountRepository.save(KakaoAccount.builder().id(kakaoAccountDto.getId()).user(user).build());
                break;
            case APPLE:
                AppleTokenDto appleTokenDto = getAppleToken(providerAuthorization);
                AppleAccountDto appleAccountDto = getAppleAccount(appleTokenDto.getId_token());
                if (appleAccountRepository.findById(appleAccountDto.getSub()).isPresent()) {
                    throw new GeneralException(Code.ALREADY_REGISTERED, userDto.getProviderType().toString());
                }

                user = userRepository.save(userDto.toEntity(userRepository));
                appleAccountRepository.save(
                    AppleAccount.builder().id(appleAccountDto.getSub()).refreshToken(appleTokenDto.getRefresh_token()).user(user)
                        .build());
                break;
            case TEST:
                user = userRepository.save(userDto.toEntity(userRepository));

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

    private AppleAccountDto getAppleAccount(String identityToken) {
        try {
            identityToken = identityToken.replace("Bearer ", "");

            ApplePublicKeyDto applePublicKey = new RestTemplate().exchange(
                "https://appleid.apple.com/auth/keys",
                HttpMethod.GET,
                new HttpEntity<>(null, null),
                ApplePublicKeyDto.class
            ).getBody();

            String headerOfIdentityToken = identityToken.substring(0, identityToken.indexOf("."));

            Map<String, String> header = new ObjectMapper().readValue(
                new String(Base64Utils.decodeFromUrlSafeString(headerOfIdentityToken), StandardCharsets.UTF_8),
                Map.class);
            ApplePublicKeyDto.Key key = applePublicKey.getMatchedKeyBy(header.get("kid"), header.get("alg"))
                .orElseThrow(() -> new NullPointerException("Failed get public key from apple's id server."));

            byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
            byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

            BigInteger n = new BigInteger(1, nBytes);
            BigInteger e = new BigInteger(1, eBytes);

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance(key.getKty());
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            Claims userInfo = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(identityToken).getBody();

            Map<String, Object> expectedMap = new HashMap<>(userInfo);

            return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .convertValue(expectedMap, AppleAccountDto.class);
        } catch (Exception e) {
            throw new GeneralException(Code.APPLE_SERVER_ERROR, e);
        }
    }

    private AppleTokenDto getAppleToken(String appleAuthorizationCode) {
        try {
            appleAuthorizationCode = appleAuthorizationCode.replace("Bearer ", "");

            HttpHeaders headers = new HttpHeaders();
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            params.add("client_id", appleClientId);
            params.add("client_secret", getAppleClientSecret());
            params.add("code", appleAuthorizationCode);
            params.add("grant_type", "authorization_code");

            return new RestTemplate().exchange(
                "https://appleid.apple.com/auth/token",
                HttpMethod.POST,
                new HttpEntity<>(params, headers),
                AppleTokenDto.class
            ).getBody();
        } catch (Exception e) {
            throw new GeneralException(Code.APPLE_SERVER_ERROR, e);
        }
    }

    public void revokeAppleAccount(Long userId) {
        try {
            String appleRefreshToken = appleAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(Code.USER_NOT_FOUND, userId.toString())).getRefreshToken();

            HttpHeaders headers = new HttpHeaders();
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

            params.add("client_id", appleClientId);
            params.add("client_secret", getAppleClientSecret());
            params.add("token", appleRefreshToken);
            params.add("token_type_hint", "refresh_token");

            new RestTemplate().exchange(
                "https://appleid.apple.com/auth/revoke",
                HttpMethod.POST,
                new HttpEntity<>(params, headers),
                String.class
            );

            appleAccountRepository.deleteByUserId(userId);
        } catch (Exception e) {
            throw new GeneralException(Code.APPLE_SERVER_ERROR, e);
        }
    }

    private String getAppleClientSecret() throws IOException {
        Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
            .setHeaderParam("kid", appleKeyId)
            .setHeaderParam("alg", "ES256")
            .setIssuer(appleTeamId)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(expirationDate)
            .setAudience("https://appleid.apple.com")
            .setSubject(appleClientId)
            .signWith(SignatureAlgorithm.ES256, getApplePrivateKey())
            .compact();
    }

    private PrivateKey getApplePrivateKey() throws IOException {
        ClassPathResource resource = new ClassPathResource(applePrivateKey);
        String privateKey = new String(resource.getInputStream().readAllBytes());
        Reader pemReader = new StringReader(privateKey);
        PEMParser pemParser = new PEMParser(pemReader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
        return converter.getPrivateKey(object);
    }
}