package com.artistack.user.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserConstraint {
    ARTISTACK_ID_MAX_LENGTH(10),
    NICKNAME_MAX_LENGTH(12),
    DESCRIPTION_MAX_LENGTH(100);

    private final Integer key;
}