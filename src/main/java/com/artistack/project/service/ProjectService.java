package com.artistack.project.service;

import com.artistack.instrument.repository.ProjectInstrumentRepository;
import com.artistack.project.domain.Project;
import com.artistack.project.dto.ProjectDto;
import com.artistack.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.artistack.base.GeneralException;
import com.artistack.base.constant.Code;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectInstrumentRepository projectInstrumentRepository;
    private final S3UploaderService s3UploaderService;

    // 초기 값 설정
//    @PostConstruct
//    public void initialize() {
//        List<String> videoUrls = List.of("https://...", "https://...", "https://...", "https://...", "https://...",
//                "https://...", "https://...", "https://...");
//        List<String> titles = List.of("제목1", "제목2", "제목3", "제목4", "제목5", "제목6", "제목7");
//        List<String> descriptions = List.of("설명1", "설명2", "설명3", "설명4", "설명5", "설명6", "설명7");
//
//        for (int i = 1; i < videoUrls.size(); i++) {
//            projectRepository.save(
//                    Project.builder().id((long) i).videoUrl(videoUrls.get(i)).title(titles.get(i)).description(descriptions.get(i))
//                            .isStackable(Boolean.TRUE).scope("ALL").codeFlow("A Dm F").bpm(100).viewCount(1)
//                            .prevProjectId((long)i).build());
//        }
//    }

    // 프로젝트 전체 조회
    public List<ProjectDto> getAll() {
        return projectRepository.findAll().stream().map(ProjectDto::response).collect(Collectors.toList());
    }

    // 프로젝트 정보 조회
    public List<ProjectDto> getById(Long projectId) {
        return projectRepository.findById(projectId).stream().map(project -> ProjectDto.getProject(project, projectInstrumentRepository))
                .collect(Collectors.toList());
    }


    // 프로젝트 게시
    public String insertProject(Long prevProjectId, MultipartFile video, ProjectDto projectDto) {
        System.out.println("ProjectService - insertProject Method");

        Boolean isInitial = prevProjectId.equals(0L);

        // validaiton: 최초 프로젝트가 아닐 경우
        if (!isInitial) {
            // 1. 이전 프로젝트가 존재하는가?
            if (!projectRepository.findById(prevProjectId).isPresent()) {
                throw new GeneralException(Code.PREV_PROJECT_NOT_EXIST, "이전 프로젝트가 존재하지 않습니다.");
            }

            // 2. 이전 프로젝트가 스택 허용인가?
            if (!projectRepository.findStackableById(prevProjectId)) {
                throw new GeneralException(Code.PREV_PROJECT_NOT_STACKABLE, "이전 프로젝트에 스택이 허용되지 않습니다.");
            }
        }

        try {
            // 동영상을 S3에 저장한 후 URL을 가져옴
            String videoUrl = s3UploaderService.uploadFile(video);
            Project project = projectRepository.save(projectDto.toEntity(videoUrl, prevProjectId));

            return project.getVideoUrl();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                throw e;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

