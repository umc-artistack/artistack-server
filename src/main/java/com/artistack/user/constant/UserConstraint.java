package com.artistack.user.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserConstraint {
    ARTISTACK_ID_MIN_LENGTH(4),
    ARTISTACK_ID_MAX_LENGTH(17),
    NICKNAME_MIN_LENGTH(1),
    NICKNAME_MAX_LENGTH(14),
    DESCRIPTION_MAX_LENGTH(38);

    private final Integer key;
}