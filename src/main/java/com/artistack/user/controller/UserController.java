package com.artistack.user.controller;

import com.artistack.base.dto.DataResponseDto;
import com.artistack.user.dto.UserDto;
import com.artistack.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Slf4j
@Api(tags = "유저 관련 API")
public class UserController {

    private final UserService userService;

    // 메이슨) 내 정보를 불러옵니다
    @ApiOperation(value = "내 정보 조회")
    @GetMapping(path = "/me")
    public DataResponseDto<Object> getMe() {
        return DataResponseDto.of(userService.getMe());
    }

    // 메이슨) artistack id로 타 유저 정보를 불러옵니다
    @ApiOperation(value = "artistack id로 타 유저 정보 조회")
    @GetMapping(path = "/{artistackId}")
    public DataResponseDto<Object> getUser(@PathVariable String artistackId) {
        return DataResponseDto.of(userService.getByArtistackId(artistackId));
    }

    // 메이슨) 내 정보를 수정합니다
    @ApiOperation(value = "내 정보 수정", notes = "내 정보를 수정합니다.")
    @PatchMapping(path = "/me")
    public DataResponseDto<Object> updateMe(
        @RequestBody UserDto userDto
    ) {
        return DataResponseDto.of(userService.updateMe(userDto));
    }

    // 메이슨) 회원 탈퇴를 진행합니다
    @ApiOperation(value = "회원 탈퇴")
    @DeleteMapping(path = "/me")
    public DataResponseDto<Object> deleteMe(
    ) {
        return DataResponseDto.of(userService.deleteMe());
    }

    // 메이슨) 유저 정보 중복 여부를 체크합니다
    @ApiOperation(value = "유저 정보 중복 여부 체크")
    @GetMapping(path = "/duplicate")
    public DataResponseDto<Object> checkDuplicate(
        @RequestParam("type") String type,
        @RequestParam("value") String value
    ) {
        return DataResponseDto.of(type.equals("artistackId") && userService.isArtistackIdDuplicated(value));
    }
}