package com.artistack.user.dto;

import static com.artistack.user.constant.UserConstraint.ARTISTACK_ID_MAX_LENGTH;
import static com.artistack.user.constant.UserConstraint.DESCRIPTION_MAX_LENGTH;
import static com.artistack.user.constant.UserConstraint.NICKNAME_MAX_LENGTH;
import static org.apache.logging.log4j.util.Strings.isBlank;

import com.artistack.base.GeneralException;
import com.artistack.base.constant.Code;
import com.artistack.instrument.domain.UserInstrument;
import com.artistack.instrument.dto.InstrumentDto;
import com.artistack.instrument.repository.UserInstrumentRepository;
import com.artistack.oauth.constant.ProviderType;
import com.artistack.user.constant.Role;
import com.artistack.user.domain.User;
import com.artistack.user.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
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

    private UserRepository userRepository;

    public UserDto() {
    }

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

    public User toEntity(UserRepository userRepository) {
        this.userRepository = userRepository;

        return User.builder().
            artistackId(validateArtistackId(artistackId)).
            nickname(validateNickname(nickname)).
            description(validateDescription(description)).
            profileImgUrl(profileImgUrl)
            .providerType(providerType).
            build();
    }

    public User updateEntity(User user, UserRepository userRepository) {
        this.userRepository = userRepository;

        Optional.ofNullable(nickname).ifPresent((str) -> user.setNickname(validateNickname(str)));
        Optional.ofNullable(description).ifPresent((str) -> user.setDescription(validateDescription(str)));
        Optional.ofNullable(profileImgUrl).ifPresent(user::setProfileImgUrl);
        return user;
    }

    private String validateArtistackId(String str) {
        // artistack id nullable, 길이 검사
        if (isBlank(str) || str.contains(" ") || str.length() > ARTISTACK_ID_MAX_LENGTH.getKey()) {
            throw new GeneralException(Code.ARTISTACK_ID_FORMAT_ERROR);
        }
        if (userRepository.findByArtistackId(str).isPresent()) {
            throw new GeneralException(Code.ARTISTACK_ID_DUPLICATED);
        }
        return str;
    }

    private String validateNickname(String str) {
        // nickname nullable, 길이 검사
        if (isBlank(str) || str.contains(" ") || str.length() > NICKNAME_MAX_LENGTH.getKey()) {
            throw new GeneralException(Code.NICKNAME_FORMAT_ERROR);
        }
        return str;
    }

    private String validateDescription(String str) {
        // description 길이 검사
        if (!isBlank(str) && str.length() > DESCRIPTION_MAX_LENGTH.getKey()) {
            throw new GeneralException(Code.USER_DESCRIPTION_FORMAT_ERROR);
        }
        return str;
    }
}

