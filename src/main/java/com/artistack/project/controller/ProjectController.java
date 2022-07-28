package com.artistack.project.controller;
import com.artistack.base.dto.DataResponseDto;
import com.artistack.project.domain.Project;
import com.artistack.project.service.ProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Api(tags = "Projects")
@RequiredArgsConstructor
@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    // 1. 프로젝트 전체 정보 조회
    @ApiOperation(value = "프로젝트 전체 조회")
    @GetMapping("")
    public DataResponseDto<Object> getAllProjects() { return DataResponseDto.of(projectService.getAll()); }

    // 2. 프로젝트 정보 조회
    @ApiOperation(value = "프로젝트 정보 조회")
    @GetMapping("/{id}/info")
    public DataResponseDto<Object> getProject(
            @PathVariable Long id
    )  {
        return DataResponseDto.of(projectService.getById(id));
    }
}
