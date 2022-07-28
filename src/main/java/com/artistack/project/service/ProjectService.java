package com.artistack.project.service;

import com.artistack.project.domain.Project;
import com.artistack.project.dto.ProjectDto;
import com.artistack.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;

    // 1. 프로젝트 전체 조회
    public List<ProjectDto> getAll() {
        return projectRepository.findAll().stream().map(ProjectDto::response).collect(Collectors.toList());
    }

    // 2. 프로젝트 정보 조회 (곡정보)
    public List<ProjectDto> getById(Long projectId) {
        return projectRepository.findById(projectId).stream().map(ProjectDto::getProject).collect(Collectors.toList());
    }

}

