package com.artistack.oauth.dto;

import com.artistack.user.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppleAccountDto {

    private String iss;
    private String exp;
    private String iat;
    private String sub; // id
    private String at_hash;
    private String email;
    private Boolean email_verified;
    private Boolean is_private_email;
    private String auth_time;
    private Boolean nonce_supported;


    public UserDto toUserDto() {
        return UserDto.builder()
            .build();
    }
}
