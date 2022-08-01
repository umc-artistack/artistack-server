package com.artistack.project.service;

import com.artistack.base.GeneralException;
import com.artistack.base.constant.Code;
import com.artistack.instrument.domain.Instrument;
import com.artistack.instrument.dto.InstrumentDto;
import com.artistack.instrument.repository.InstrumentRepository;
import com.artistack.project.domain.Project;
import com.artistack.project.dto.ProjectDto;
import com.artistack.project.repository.ProjectRepository;
import com.artistack.user.domain.User;
import com.artistack.user.dto.UserDto;
import com.artistack.user.repository.UserRepository;
import com.artistack.util.SecurityUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final S3UploaderService s3UploaderService;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final InstrumentRepository instrumentRepository;

    public String insertProject(Long prevProjectId, MultipartFile video, ProjectDto projectDto) {

        Boolean isInitial = prevProjectId.equals(0L);

        // validation: 최초 프로젝트가 아닐 경우
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

            Instrument instrument = instrumentRepository.findById(projectDto.getInstrument())
                .orElseThrow(() -> new GeneralException(Code.INVALID_INSTRUMENT, "올바른 악기를 선택해주세요."));
            InstrumentDto instrumentDto = InstrumentDto.response(instrument);

            User user = userRepository.findById(SecurityUtil.getUserId())
                .orElseThrow(() -> new GeneralException(Code.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));

            Project project = projectRepository.save(projectDto.toEntity(videoUrl, prevProjectId, instrumentDto, user));

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

    public List<UserDto> getStackers(Long projectId, String sequence) {
        if (sequence.equals("prev")) {
            return getPrevStackers(projectId);
        }

        else {
            return getNextStackers(projectId);
        }
    }

    public List<UserDto> getPrevStackers(Long projectId) {
        ArrayList<UserDto> test = new ArrayList<>();

        // prevProjectId = 0이 될 때까지 타고 들어가
        /**
         * while(prevProjectId == 0) {
         * 프로젝트에서 user_id 추출
         * user_id로부터 닉네임, 프로필, 악기를 가져와
         * 리스트에 추가해
         * }
        **/

        return test;
    }

    public List<UserDto> getNextStackers(Long projectId) {
        ArrayList<UserDto> stackers = new ArrayList<>();

        // prevProjectId가 projectId인 자식 노드들 반환
        // 1. prevProjectId가 projectId인 프로젝트들을 찾아
        List<Project> projects = projectRepository.findAllByPrevProjectId(projectId);

        // 프로젝트에서 user_id만 추출해 > user_id로부터 닉네임, 프로필, 악기를 가져와서 { } 형식으로 만들어
        for (Project project : projects) {
            Long userId = project.getUser().getId();
            Instrument instrument = project.getInstrument();

            User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(Code.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));
            UserDto userDto = UserDto.stackResponse(user, instrument);
            stackers.add(userDto);
        }

        return stackers;

    }
}
