package com.artistack.oauth.controller;

import com.artistack.base.dto.DataResponseDto;
import com.artistack.base.constant.Code;
import com.artistack.jwt.dto.JwtDto;
import com.artistack.oauth.constant.ProviderType;
import com.artistack.oauth.service.OAuthService;
import com.artistack.user.dto.UserDto;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/oauth")
@Slf4j
public class OAuthController {

    private final OAuthService oAuthService;

    @ApiOperation(value = "로그인")
    @GetMapping("/signIn")
    public DataResponseDto<Object> loginWithProviderToken(
        @RequestParam("providerType") ProviderType providerType,
        @RequestHeader("Authorization") String providerAccessToken
    ) {
        Object signInResult = oAuthService.signIn(providerType, providerAccessToken);
        if(signInResult.getClass().equals(JwtDto.class))
            return DataResponseDto.of(signInResult);
        return DataResponseDto.of(Code.NOT_REGISTERED, signInResult);
    }

    @ApiOperation(value = "회원 가입")
    @ApiImplicitParam(name = "providerType", value = "회원가입 타입", required = true, dataType = "string", paramType = "query")
    @GetMapping("/signUp")
    public DataResponseDto<Object> signUp(
        @RequestHeader("Authorization") String providerAccessToken,
        @RequestBody UserDto userDto
    ) {
        return DataResponseDto.of(oAuthService.signUp(userDto, providerAccessToken));
    }
}
