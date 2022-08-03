package com.artistack.project.controller;

import com.artistack.base.GeneralException;
import com.artistack.base.constant.Code;
import com.artistack.base.dto.DataResponseDto;
import com.artistack.project.dto.ProjectDto;
import com.artistack.project.service.ProjectService;
import lombok.RequiredArgsConstructor;


import io.swagger.annotations.ApiImplicitParam;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "Projects")
@RequiredArgsConstructor
@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    /**
     *  프로젝트 전체 조회 API - 셀리나 (탐색)
     *  [GET] /projects
     *  후순위 개발
     */
    @ApiOperation(value = "프로젝트 전체 조회")
    @GetMapping("")
    public DataResponseDto<Object> getAllProjects() { return DataResponseDto.of(projectService.getAll()); }

    /**
     *  프로젝트 정보 조회 API - 셀리나
     *  [Post] /projects/{projectId}
     */
    @ApiOperation(value = "프로젝트 정보 조회")
    @GetMapping("/{id}/info")
    public DataResponseDto<Object> getProject(
            @PathVariable Long id
    )  {
        return DataResponseDto.of(projectService.getById(id));
    }

    /**
     *  프로젝트 게시 API - 제이
     *  [Post] /projects/{prevProjectId}
     */

    @ApiOperation(
        value = "프로젝트 등록",
        notes = "이전 프로젝트가 없는 경우 prevProjectId를 0으로 해주세요"
    )
    @ApiImplicitParam(name = "prevProjectId", value = "이전 프로젝트 id", dataType = "integer", defaultValue = "0")
    @PostMapping(value = "/{prevProjectId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DataResponseDto<Object> uploadProject(
        @PathVariable Long prevProjectId,
        @RequestPart(value = "video") MultipartFile video,
        @RequestPart(value = "dto") @Parameter(schema =@Schema(type = "string", format = "binary")) ProjectDto projectDto
        ) {
        // validation: 비디오 파일이 비어 있을 경우
        if (video.isEmpty()) {
            throw new GeneralException(Code.EMPTY_VIDEO, "비디오 파일이 비어있습니다.");
        }
        // validation: 프로젝트 제목이 최대 글자 수를 넘겼을 경우 validation
        if (projectDto.getTitle().length() > 18) {
            throw new GeneralException(Code.TITLE_TOO_LONG, "연주 제목이 최대 글자(18자)를 초과했습니다.");
        }

        // validation: 연주 정보가 최대 글자 수를 넘겼을 경우
        if (projectDto.getDescription().length() > 48) {
            throw new GeneralException(Code.DESCRIPTION_TOO_LONG, "연주에 대한 설명이 최대 글자(48자)를 초과했습니다.");
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
