package com.artistack.user.service;


import com.artistack.base.GeneralException;
import com.artistack.base.constant.Code;
import com.artistack.instrument.repository.UserInstrumentRepository;
import com.artistack.jwt.repository.JwtRepository;
import com.artistack.oauth.service.OAuthService;
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

    private final OAuthService oAuthService;
    private final UserRepository userRepository;
    private final JwtRepository jwtRepository;
    private final UserInstrumentRepository userInstrumentRepository;

    private User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new GeneralException(Code.UNAUTHORIZED));
    }


    /**
     * 메이슨) 회원 정보를 조회합니다
     *
     * @param userId 조회할 유저 id
     * @return 유저 dto
     */
    public UserDto get(Long userId) {
        return UserDto.baseResponse(getUser(userId), userInstrumentRepository);
    }

    public UserDto getMe() {
        return get(SecurityUtil.getUserId());
    }

    public UserDto getByArtistackId(String artistackId) {
        Long id = userRepository.findByArtistackId(artistackId)
            .orElseThrow(() -> new GeneralException(Code.USER_NOT_FOUND, artistackId)).getId();
        return get(id);
    }


    /**
     * 메이슨) 내 정보를 업데이트합니다
     *
     * @param userDto 업데이트 할 body
     * @return 업데이트된 유저 정보
     */
    public UserDto updateMe(UserDto userDto) {
        User me = getUser(SecurityUtil.getUserId());
        me = userRepository.save(userDto.updateEntity(me, userRepository));
        return UserDto.baseResponse(me);
    }


    /**
     * 메이슨) 회원탈퇴를 진행합니다
     *
     * @param userId 탈퇴할 유저 id
     * @return 탈퇴 성공 시 true
     */
    public Boolean delete(Long userId) {
        User user = getUser(userId);

        switch (user.getProviderType()) {
            case KAKAO:
                oAuthService.unlinkKakaoAccount(userId);
                break;
            case APPLE:
                break;
        }

        jwtRepository.deleteByUserId(user.getId());
        user.withdraw();

        return true;
    }

    public Boolean deleteMe() {
        return delete(SecurityUtil.getUserId());
    }


    public boolean isArtistackIdDuplicated(String artistackId) {
        return userRepository.findByArtistackId(artistackId).isPresent();
    }
}
