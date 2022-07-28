package com.artistack.project.controller;

import com.artistack.base.GeneralException;
import com.artistack.base.constant.Code;
import com.artistack.base.dto.DataResponseDto;
import com.artistack.project.dto.ProjectDto;
import com.artistack.project.service.ProjectService;
import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    /**
     *  프로젝트 게시 API - 제이
     *  [Post] /projects/{prevProjectId}
     */
    @PostMapping("/{prevProjectId}")
    public DataResponseDto<Object> uploadProject(
        @PathVariable Long prevProjectId,
        @RequestPart(value = "video") MultipartFile video,
        @RequestPart(value = "dto") ProjectDto projectDto
        ) {

        // TODO: 유저 인덱스 저장하는 로직 만들기

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
