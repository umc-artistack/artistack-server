package com.artistack.project.controller;

import com.artistack.base.GeneralException;
import com.artistack.base.constant.Code;
import com.artistack.base.dto.DataResponseDto;
import com.artistack.project.dto.ProjectDto;
import com.artistack.project.service.ProjectService;
import com.artistack.user.dto.UserDto;
import com.artistack.util.SecurityUtil;
import io.swagger.annotations.ApiImplicitParams;
import java.util.List;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RequiredArgsConstructor
@RestController
@RequestMapping("/projects")
@Slf4j
@Api(tags = "프로젝트 관련 API ")
public class ProjectController {
    // TODO: 모든 API에 header 적용

    private final ProjectService projectService;

    /**
     *  프로젝트 정보 조회 API - 셀리나
     *  [Get] /projects/{projectId}
     */
    @ApiOperation(value = "프로젝트 정보 조회", notes = "단일 프로젝트를 조회합니다. projectId를 입력해주세요.")
    @ApiImplicitParam(name = "projectId", value = "정보를 조회할 프로젝트 id", required = true, dataType = "long", paramType = "path")
    @GetMapping("/{projectId}")
    public DataResponseDto<Object> getProject(
            @PathVariable Long projectId
    )  {
        return DataResponseDto.of(projectService.getById(projectId));
    }

    /**
     *  조건, 페이징과 함께 프로젝트 정보 조회 API - 메이슨
     *  [Get] /projects/search
     */
    @ApiOperation(value = "조건, 페이징과 함께 프로젝트 정보 조회")
    @GetMapping("/search")
    public DataResponseDto<Object> getProjectsByConditionWithPaging(
        Pageable pageable,
        @RequestParam Optional<String> artistackId,
        @RequestParam Optional<Long> lastId
    ) {
        return DataResponseDto.of(projectService.getByConditionWithPaging(pageable, artistackId, lastId));
    }
    /**
     *  페이징과 함께 나의 프로젝트 정보 조회 API - 메이슨
     *  [Get] /projects/me
     */
    @ApiOperation(value = "페이징과 함께 나의 프로젝트 정보 조회")
    @GetMapping("/me")
    public DataResponseDto<Object> getMyProjectsByConditionWithPaging(
        Pageable pageable
    ) {
        return DataResponseDto.of(projectService.getMyWithPaging(pageable));
    }

    /**
     *  나의 프로젝트 삭제 API - 메이슨
     *  [Delete] /projects/{projectId}
     */
    @ApiOperation(
        value = "프로젝트 삭제"
    )
    @ApiImplicitParam(name = "projectId", value = "프로젝트 id", dataType = "integer")
    @DeleteMapping(value = "/{projectId}")
    public DataResponseDto<Object> deleteMyProject(
        @PathVariable Long projectId
    ) {
        return DataResponseDto.of(projectService.deleteMyProject(projectId));
    }


    /**
     *  프로젝트 좋아요 등록 API - 셀리나
     *  [Post] /projects/{projectId}/like
     */
    @ApiOperation(
            value = "프로젝트 좋아요 등록"
    )
    @ApiImplicitParam(name = "projectId", value = "프로젝트 id", dataType = "integer")
    @PostMapping(value = "/{projectId}/like")
    public DataResponseDto<Object> likeProject(
            @PathVariable Long projectId
    ) {
        try {
            return DataResponseDto.of(projectService.likeProject(projectId));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     *  프로젝트 좋아요 취소 API - 셀리나
     *  [DELETE] /projects/{projectId}/like
     */
    @ApiOperation(
            value = "프로젝트 좋아요 취소"
    )
    @ApiImplicitParam(name = "projectId", value = "프로젝트 id", dataType = "integer")
    @DeleteMapping(value = "/{projectId}/like")
    public DataResponseDto<Object> deleteLikeProject(
            @PathVariable Long projectId
    ) {
        return DataResponseDto.of(projectService.deleteLikeProject(projectId));
    }

    /**
     *  프로젝트 좋아요한 사람 조회 API - 셀리나
     *  [Get] /projects/{projectId}/like/users
     */
    @ApiOperation(value = "프로젝트 정보 조회", notes = "단일 프로젝트를 조회합니다. projectId를 입력해주세요.")
    @ApiImplicitParam(name = "projectId", value = "정보를 조회할 프로젝트 id", required = true, dataType = "long", paramType = "path")
    @GetMapping("/{projectId}/like/users")
    public DataResponseDto<Object> getProjectLikeUsers(
            Pageable pageable,
            @PathVariable Long projectId
    )  {
        return DataResponseDto.of(projectService.getProjectLikeUsersWithPaging(pageable, projectId));
    }

    /**
     *  스택 조회 API - 제이
     *  [Get] /projects/{projectId}/prev?current=true
     *  [Get] /projects/{projectId}/next?current=true
     */
    @ApiOperation(value = "스택 조회")
    @ApiImplicitParams( value = {
        @ApiImplicitParam(name = "projectId", value = "현재 프로젝트 id", required = true, dataType = "long", paramType = "path"),
        @ApiImplicitParam(name = "sequence", value = "순서(prev or next)", required = true, dataType = "string", paramType = "path")})
    @GetMapping("/{projectId}/{sequence}")
    public DataResponseDto<Object> getStack(
        @PageableDefault(size = 6) Pageable pageable,
        @PathVariable Long projectId, @PathVariable String sequence,
        @RequestParam(defaultValue = "true") Boolean current) {
        // validation
        // 1. query parameter가 next, prev를 제외한 다른 값이 들어올 경우
        if (!(sequence.equals("next") || sequence.equals("prev"))) {
            throw new GeneralException(Code.INVALID_SEQUENCE, "sequence는 prev나 next만 사용할 수 있습니다.");
        }

        try {
            List<UserDto> stackers = projectService.getStackers(projectId, sequence, current);

            final int start = (int) pageable.getOffset();
            final int end = Math.min((start + pageable.getPageSize()), stackers.size());
            final Page<UserDto> page = new PageImpl<>(stackers.subList(start, end), pageable, stackers.size());

            return DataResponseDto.of(page);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     *  프로젝트 게시 API - 제이
     *  [Post] /projects/{prevProjectId}
     */
    @ApiOperation(
        value = "프로젝트 등록",
        notes = "최초 프로젝트 등록일 경우, prevProjectId를 0으로 해주세요. 다른 프로젝트 위에 프로젝트를 쌓는 경우, 해당 프로젝트의 id를 입력해주세요.<br>"
            + "번거롭지만 dto는 .json 파일로 업로드해주세요..! 포스트맨에서 테스트할 때는, Content-Type을 application/json으로 설정하여 텍스트로 입력할 수 있습니다.<br>"
            + "scope는 0부터 시작하며, 숫자, 문자열 중 편한 것을 선택하여 입력해주세요. 0은 전체 공개(PUBLIC), 1은 비공개(PRIVATE)입니다."
    )
    @ApiImplicitParam(name = "prevProjectId", value = "이전 프로젝트 id", dataType = "long", defaultValue = "0")
    @PostMapping(value = "/{prevProjectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DataResponseDto<Object> uploadProject(
        @PathVariable Long prevProjectId,
        @RequestPart(value = "video") MultipartFile video,
        @RequestPart(value = "dto") @Parameter(schema =@Schema(type = "string", format = "binary")) ProjectDto projectDto
        ) {
        // validation: 비디오 파일이 비어 있을 경우
        if (video.isEmpty()) {
            throw new GeneralException(Code.EMPTY_VIDEO, "비디오 파일이 비어있습니다.");
        }
        // validation: 프로젝트 제목이 최대 글자 수를 넘겼을 경우
        if (projectDto.getTitle().length() > 18) {
            throw new GeneralException(Code.TITLE_TOO_LONG, "연주 제목이 최대 글자(18자)를 초과했습니다.");
        }

        // validation: 연주 정보가 최대 글자 수를 넘겼을 경우
        if (projectDto.getDescription().length() > 48) {
            throw new GeneralException(Code.DESCRIPTION_TOO_LONG, "연주에 대한 설명이 최대 글자(48자)를 초과했습니다.");
        }

        // validation: 올바르지 않은 악기 id를 사용했을 경우
        for (Long instrumentId : projectDto.getInstrumentIds()) {
            if (instrumentId < 1 || instrumentId > 6) {
                throw new GeneralException(Code.INVALID_INSTRUMENT, "올바른 악기를 선택해주세요.");
            }
        }

        // validation: 2개 이상의 악기 id를 사용했을 경우
        if (projectDto.getInstrumentIds().size() > 1) {
            throw new GeneralException(Code.MULTI_INSTRUMENT_ERROR, "하나의 악기만 선택할 수 있습니다.");
        }

        try {

            String videoUrl = projectService.insertProject(prevProjectId, video, projectDto);

            return DataResponseDto.of(videoUrl);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
