package com.artistack.project.service;

import com.artistack.project.domain.Project;
import com.artistack.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    // 1. 프로젝트 전체 조회 (탐색)
    public List<Project> findProjects() { return projectRepository.findAll();}

    // 2. 프로젝트 정보 조회 (곡정보)
    public List<Project> findProject(Long id) {return projectRepository.findByProjectId(id);}

}

