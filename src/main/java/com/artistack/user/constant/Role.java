package com.artistack.user.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_USER"), ADMIN("ROLE_ADMIN"), WITHDRAWAL("ROLE_WITHDRAWAL"), BANNED("ROLE_BANNED");

    private final String key;
}