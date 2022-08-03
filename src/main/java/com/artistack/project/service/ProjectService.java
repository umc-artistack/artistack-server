package com.artistack.project.service;

import com.artistack.base.GeneralException;
import com.artistack.base.constant.Code;
import com.artistack.instrument.domain.Instrument;
import com.artistack.instrument.dto.InstrumentDto;
import com.artistack.instrument.repository.InstrumentRepository;
import com.artistack.instrument.repository.ProjectInstrumentRepository;
import com.artistack.project.domain.Project;
import com.artistack.project.dto.ProjectDto;
import com.artistack.project.repository.ProjectRepository;
import com.artistack.user.domain.User;
import com.artistack.user.repository.UserRepository;
import com.artistack.util.SecurityUtil;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {
    private final S3UploaderService s3UploaderService;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final InstrumentRepository instrumentRepository;
    private final ProjectInstrumentRepository projectInstrumentRepository;

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
    @Transactional
    public String insertProject(Long prevProjectId, MultipartFile video, ProjectDto projectDto) {

        boolean isInitial = prevProjectId.equals(0L);

        // validaiton: 최초 프로젝트가 아닐 경우
        if (!isInitial) {
            // 1. 이전 프로젝트가 존재하는가?
            if (projectRepository.findById(prevProjectId).isEmpty()) {
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
//            String videoUrl = "테스트";

            List<InstrumentDto> instruments = projectDto.getInstruments();

            User user = userRepository.findById(SecurityUtil.getUserId())
                .orElseThrow(() -> new GeneralException(Code.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));

            Project project = projectRepository.save(projectDto.toEntity(videoUrl, prevProjectId, user));

            for (InstrumentDto instrumentDto : instruments) {
                Instrument instrument = instrumentRepository.findById(instrumentDto.getId())
                        .orElseThrow(() -> new GeneralException(Code.INVALID_INSTRUMENT, "올바른 악기를 선택해주세요."));
                projectInstrumentRepository.save(instrumentDto.toEntity(project, instrument));
            }

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
