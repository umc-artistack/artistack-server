package com.artistack.oauth.dto;

import com.artistack.user.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KakaoAccountDto {

    private String id;
    private KakaoAccount kakao_account;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class KakaoAccount {

        private Profile profile;
        private String email;
        private String gender;

        @Data
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Profile {

            private String nickname;
            private String profile_image_url;
        }
    }

    public UserDto toUserDto() {
        return UserDto.builder()
            .profileImgUrl(kakao_account.getProfile().getProfile_image_url())
            .nickname(kakao_account.getProfile().getNickname())
            .build();
    }
}
