package com.artistack.oauth.constant;

import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProviderType {
    KAKAO("kakao"), APPLE("apple"), TEST("test");

    private final String key;
}