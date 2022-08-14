package com.artistack.project.service;

import com.artistack.base.GeneralException;
import com.artistack.base.constant.Code;
import com.artistack.instrument.domain.Instrument;
import com.artistack.instrument.domain.ProjectInstrument;
import com.artistack.instrument.dto.InstrumentDto;
import com.artistack.instrument.repository.InstrumentRepository;
import com.artistack.instrument.repository.ProjectInstrumentRepository;
import com.artistack.project.domain.Project;
import com.artistack.project.domain.ProjectLike;
import com.artistack.project.dto.ProjectDto;
import com.artistack.project.repository.ProjectLikeRepository;
import com.artistack.project.repository.ProjectRepository;
import com.artistack.user.domain.User;

import com.artistack.user.repository.UserRepository;
import com.artistack.util.SecurityUtil;
import java.io.IOException;
import com.artistack.user.dto.UserDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final ProjectLikeRepository projectLikeRepository;

    // 프로젝트 전체 조회
    public List<ProjectDto> getAll() {
        return projectRepository.findAll().stream().map(ProjectDto::projectResponse).collect(Collectors.toList());
    }

    // 프로젝트 정보 조회
    public ProjectDto getById(Long projectId) {
        return projectRepository.findById(projectId)
            .map(project -> ProjectDto.projectResponse(project, projectInstrumentRepository))
            .orElseThrow(() -> new GeneralException(Code.PROJECT_NOT_FOUND, "프로젝트를 찾을 수 없습니다."));
    }

    /**
     * 메이슨) 조건에 맞는 프로젝트들을 페이징 기능과 함께 반환합니다
     *
     * @param artistackId 조회할 유저의 artistackId (optional)
     * @return 조건에 맞는 프로젝트들 (profileResponse)
     */
    public Page<ProjectDto> getByConditionWithPaging(Pageable pageable, Optional<String> artistackId) {
        return projectRepository.getByConditionWithPaging(pageable, artistackId.orElse(null))
            .map(ProjectDto::profileResponse);
    }

    /**
     * 메이슨) 내 프로젝트들을 페이징 기능과 함께 반환합니다
     *
     * @return 내 프로젝트들 (profileResponse)
     */
    public Page<ProjectDto> getMyWithPaging(Pageable pageable) {
        String artistackId = userRepository.findById(SecurityUtil.getUserId())
            .orElseThrow(() -> new GeneralException(Code.USER_NOT_FOUND)).getArtistackId();
        return getByConditionWithPaging(pageable, Optional.of(artistackId));
    }

    // 프로젝트 좋아요 등록
    @Transactional
    public String likeProject(Long projectId) {
        try {

            User user = userRepository.findById(SecurityUtil.getUserId())
                    .orElseThrow(() -> new GeneralException(Code.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));

            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new GeneralException(Code.PROJECT_NOT_FOUND, "프로젝트를 찾을 수 없습니다."));

            if (projectLikeRepository.findByUserAndProject(user,project).isPresent()) {
                throw new GeneralException(Code.PROJECT_LIKE_EXIST, "프로젝트 좋아요가 이미 존재합니다.");
            }

            ProjectLike projectLike = projectLikeRepository.save(ProjectLike.of(user, project));

            // projectLike.getId();

            return "좋아요 등록이 완료되었습니다.";

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // 프로젝트 좋아요 취소
    public String deleteLikeProject(Long projectId) {
        User user = userRepository.findById(SecurityUtil.getUserId())
                .orElseThrow(() -> new GeneralException(Code.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new GeneralException(Code.PROJECT_NOT_FOUND, "프로젝트를 찾을 수 없습니다."));

        if (projectLikeRepository.findByUserAndProject(user,project).isEmpty()) {
            throw new GeneralException(Code.PROJECT_LIKE_NOT_EXIST, "취소할 프로젝트 좋아요가 존재하지 않습니다.");
        }

        projectLikeRepository.deleteByUserAndProject(user,project);

        return "좋아요 취소가 완료되었습니다.";
    }

    // 프로젝트 게시
    @Transactional
    public String insertProject(Long prevProjectId, MultipartFile video, ProjectDto projectDto) {

        boolean isInitial = prevProjectId.equals(0L);

        // validation: 최초 프로젝트가 아닐 경우
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

        List<Long> instrumentIds = projectDto.getInstrumentIds();

        User user = userRepository.findById(SecurityUtil.getUserId())
            .orElseThrow(() -> new GeneralException(Code.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));

        // 동영상을 S3에 저장한 후 URL을 가져옴
        String videoUrl = null;
        try {
            videoUrl = s3UploaderService.uploadFile(video);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//            String videoUrl = "테스트";

        Project project = projectRepository.save(projectDto.toEntity(videoUrl, prevProjectId, user));

        for (Long instrumentId : instrumentIds) {
            Instrument instrument = instrumentRepository.findById(instrumentId)
                .orElseThrow(() -> new GeneralException(Code.INVALID_INSTRUMENT,
                    "Controller validation failed - API 담당자에게 말해주세요!"));
            projectInstrumentRepository.save(
                new InstrumentDto(instrument.getId(), instrument.getName(), instrument.getImgUrl())
                    .toEntity(project, instrument)
            );
        }

        return project.getVideoUrl();
    }

    // 스택 조회 - 다음 스택인지, 이전 스택인지 선택
    public List<UserDto> getStackers(Long projectId, String sequence) {
        if (sequence.equals("prev")) {
            return getPrevStackers(projectId);
        } else {
            return getNextStackers(projectId);
        }
    }

    // 이전에 스택을 쌓은 유저 목록(이전 스택) 조회
    public List<UserDto> getPrevStackers(Long projectId) {
        // while문을 이용하여 prevProjectId = 0(최초 프로젝트)이 될 때까지 스택 조회
        ArrayList<UserDto> stackers = new ArrayList<>();

        Long prevProjectId = projectRepository.findById(projectId)
            .orElseThrow(() -> new GeneralException(Code.PROJECT_NOT_FOUND, "프로젝트를 찾을 수 없습니다."))
            .getPrevProjectId();

        while (prevProjectId != 0) {
            Project project = projectRepository.findById(prevProjectId)
                .orElseThrow(() -> new GeneralException(Code.PROJECT_NOT_FOUND, "프로젝트를 찾을 수 없습니다."));

            User user = project.getUser();

            List<InstrumentDto> instruments = getInstrumentDtoFromProject(project);

            UserDto userDto = UserDto.stackResponse(user, instruments);
            stackers.add(userDto);

            prevProjectId = project.getPrevProjectId();
        }

        return stackers;
    }

    // 현재 스택 위에 스택을 쌓은 유저 목록(다음 스택) 조회
    public List<UserDto> getNextStackers(Long projectId) {
        ArrayList<UserDto> stackers = new ArrayList<>();

        // prevProjectId가 projectId인 자식 노드들 반환
        // 1. prevProjectId가 projectId인 프로젝트들을 찾아
        List<Project> projects = projectRepository.findAllByPrevProjectId(projectId);

        for (Project project : projects) {
            Long userId = project.getUser().getId();

            List<InstrumentDto> instruments = getInstrumentDtoFromProject(project);

            // userDto는 ListInstrumentDto(id, name, imgUrl인데 id만 반환할거임)를 사용
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(Code.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));

            UserDto userDto = UserDto.stackResponse(user, instruments);
            stackers.add(userDto);
        }

        return stackers;

    }

    // 스택 조회 메서드(getPrevStackers, getNextStackers)에서 중복되는 부분 모듈화
    // Project > InstrumentDto
    private List<InstrumentDto> getInstrumentDtoFromProject(Project project) {
        List<ProjectInstrument> projectInstrumentList = project.getInstruments();

        List<InstrumentDto> instruments = new ArrayList<>();
        for (ProjectInstrument projectInstrument : projectInstrumentList) {
            instruments.add(InstrumentDto.response(projectInstrument.getInstrument()));
        }

        return instruments;
    }
}
