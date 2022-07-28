package com.artistack.project.controller;
import com.artistack.base.dto.DataResponseDto;
import com.artistack.project.domain.Project;
import com.artistack.project.service.ProjectService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    // 1. 프로젝트 전체 정보 조회
    @GetMapping("")
    public AllProjectResult getAllProject(){
        List<Project> findProject = projectService.findProjects();
        List<ProjectDto> collect = findProject.stream()
                .map(m->new ProjectDto(m.getVideoUrl(),m.getCodeFlow(),m.getBpm()))
                .collect((Collectors.toList()));
        return new AllProjectResult(collect.size(), collect);
    }
    @Data
    @AllArgsConstructor
    static class AllProjectResult<T> {
        private int count;
        private T data;
    }
    @Data
    @AllArgsConstructor
    static class ProjectDto {
        private String videoUrl;
        private String codeFlow;
        private int bpm;
    }

    // 2. 프로젝트 정보 조회
    @GetMapping("/{id}/info")
    public ProjectInfoResult getProjectInfo(@PathVariable Long id){
        List<Project> findProject = projectService.findProject(id);
        List<ProjectInfoDto> collect = findProject.stream()
                .map(m->new ProjectInfoDto(m.getCodeFlow(),m.getBpm()))
                .collect((Collectors.toList()));
        return new ProjectInfoResult(collect);
    }
    @Data
    @AllArgsConstructor
    static class ProjectInfoResult<T> {
        private T data;
    }
    @Data
    @AllArgsConstructor
    static class ProjectInfoDto {
        private String codeFlow;
        private int bpm;
    }
}
