package com.artistack.user.dto;

import com.artistack.instrument.domain.UserInstrument;
import com.artistack.instrument.dto.InstrumentDto;
import com.artistack.instrument.repository.UserInstrumentRepository;
import com.artistack.oauth.constant.ProviderType;
import com.artistack.user.constant.Role;
import com.artistack.user.domain.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private String id;
    private String artistackId;
    private String nickname;
    private String description;
    private String profileImgUrl;
    private List<InstrumentDto> instruments;
    private ProviderType providerType;
    private Role role;


    public static UserDto baseResponse(User user) {
        return baseResponse(user, null);
    }

    public static UserDto baseResponse(User user, UserInstrumentRepository userInstrumentRepository) {
        return UserDto.builder()
            .artistackId(user.getArtistackId())
            .nickname(user.getNickname())
            .description(user.getDescription())
            .profileImgUrl(user.getProfileImgUrl())
            .instruments(Optional.ofNullable(userInstrumentRepository).map(
                e -> e.findByUserId(user.getId()).stream().map(UserInstrument::getInstrument)
                    .map(InstrumentDto::response).collect(Collectors.toList())).orElse(null))
            .providerType(user.getProviderType())
            .role(user.getRole())
            .build();
    }

    public static UserDto idResponse(User user) {
        return UserDto.builder()
            .artistackId(user.getArtistackId())
            .providerType(user.getProviderType())
            .role(user.getRole())
            .build();
    }

    public User toEntity() {
        return User.builder().
            artistackId(artistackId).
            nickname(nickname).
            description(description).
            profileImgUrl(profileImgUrl)
            .providerType(providerType).
            build();
    }
}

