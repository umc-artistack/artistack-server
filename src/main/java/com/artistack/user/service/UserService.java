package com.artistack.user.service;


import static com.artistack.user.constant.UserConstraint.ARTISTACK_ID_MAX_LENGTH;
import static com.artistack.user.constant.UserConstraint.DESCRIPTION_MAX_LENGTH;
import static com.artistack.user.constant.UserConstraint.NICKNAME_MAX_LENGTH;
import static org.apache.logging.log4j.util.Strings.isBlank;

import com.artistack.base.GeneralException;
import com.artistack.base.constant.Code;
import com.artistack.user.domain.User;
import com.artistack.user.dto.UserDto;
import com.artistack.user.repository.UserRepository;
import com.artistack.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * 메이슨) 회원가입, 회원정보수정 시 dto 유효성 검사를 수행합니다.
     *
     * @param userDto request body
     */
    public void validateRequest(UserDto userDto) {
        // artistack id nullable, 길이 검사
        if (isBlank(userDto.getArtistackId()) || userDto.getArtistackId().contains(" ")
            || userDto.getArtistackId().length() > ARTISTACK_ID_MAX_LENGTH.getKey()) {
            throw new GeneralException(Code.ARTISTACK_ID_FORMAT_ERROR);
        }
        // artistack id 중복 검사
        if (userRepository.findByArtistackId(userDto.getArtistackId()).isPresent()) {
            throw new GeneralException(Code.ARTISTACK_ID_DUPLICATED);
        }

        // nickname nullable, 길이 검사
        if (isBlank(userDto.getNickname()) || userDto.getArtistackId().contains(" ")
            || userDto.getNickname().length() > NICKNAME_MAX_LENGTH.getKey()) {
            throw new GeneralException(Code.NICKNAME_FORMAT_ERROR);
        }

        // description 길이 검사
        if (!isBlank(userDto.getDescription()) && userDto.getDescription().length() > DESCRIPTION_MAX_LENGTH.getKey()) {
            throw new GeneralException(Code.USER_DESCRIPTION_FORMAT_ERROR);
        }
    }
}
