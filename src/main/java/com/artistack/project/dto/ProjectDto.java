package com.artistack.project.dto;

import static org.springframework.util.ObjectUtils.isEmpty;

import com.artistack.instrument.domain.ProjectInstrument;
import com.artistack.instrument.dto.InstrumentDto;
import com.artistack.instrument.repository.ProjectInstrumentRepository;
import com.artistack.project.constant.Scope;
import com.artistack.project.domain.Project;

import com.artistack.project.repository.ProjectLikeRepository;
import com.artistack.user.domain.User;
import com.artistack.user.dto.UserDto;
import com.artistack.user.repository.UserRepository;

import com.artistack.util.SecurityUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import static org.springframework.util.ObjectUtils.isEmpty;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
@AllArgsConstructor
public class ProjectDto {

    private Long id;

    private String videoUrl;

    private String title;

    private String description;

    private String bpm;

    private String codeFlow;

    private Scope scope;

    private Boolean isStackable;

    private Integer viewCount;

    private Long prevProjectId;

    private UserDto user;

    private List<InstrumentDto> instruments;

    private List<Long> instrumentIds;

    private List<UserDto> prevStackers;

    private Integer prevStackCount;

    private Integer likeCount;

    private Integer stackCount;

    private Boolean isLiked;

    public ProjectDto() {

    }

    // 메이슨
    public static ProjectDto profileResponse(Project project, ProjectLikeRepository projectLikeRepository,
        UserRepository userRepository) {

        Boolean isLiked = !isEmpty(projectLikeRepository) && !projectLikeRepository.findByUserAndProject(
            userRepository.findById(SecurityUtil.getUserId()).orElse(null), project).isEmpty();

        return ProjectDto.builder()
            .id(project.getId())
            .title(project.getTitle())
            .videoUrl(project.getVideoUrl())
            .stackCount(project.getStackCount())
            .viewCount(project.getViewCount())
            .likeCount(project.getLikeCount())
            .isLiked(isLiked)
            .build();
    }

    public static ProjectDto projectResponse(Project project) {
        return projectResponse(project, null, null, null, null);
    }

    public static ProjectDto projectResponse(Project project, ProjectInstrumentRepository projectInstrumentRepository,
        ProjectLikeRepository projectLikeRepository, UserRepository userRepository) {
        return projectResponse(project, projectInstrumentRepository, projectLikeRepository, userRepository, null);
    }

    public static ProjectDto projectResponse(Project project, ProjectInstrumentRepository projectInstrumentRepository,
        ProjectLikeRepository projectLikeRepository, UserRepository userRepository, List<UserDto> prevStackers) {

        Boolean isLiked = !isEmpty(projectLikeRepository) && !projectLikeRepository.findByUserAndProject(
            userRepository.findById(SecurityUtil.getUserId()).orElse(null), project).isEmpty();

        return ProjectDto.builder()
            .id(project.getId())
            .videoUrl(project.getVideoUrl())
            .title(project.getTitle())
            .description(project.getDescription())
            .isStackable(project.getIsStackable())
            .scope(project.getScope())
            .codeFlow(project.getCodeFlow())
            .bpm(project.getBpm())
            .viewCount(project.getViewCount())
            .prevProjectId(project.getPrevProjectId())
            .user(UserDto.previewResponse(project.getUser()))
            .instruments(Optional.ofNullable(projectInstrumentRepository).map(
                e -> e.findByProjectId(project.getId()).stream().map(ProjectInstrument::getInstrument)
                    .map(InstrumentDto::response).collect(Collectors.toList())).orElse(null))
            .prevStackers(prevStackers)
            .prevStackCount(prevStackers.size())
            .stackCount(project.getStackCount())
            .likeCount(project.getLikeCount())
            .isLiked(isLiked)
            .build();

    }

    public static ProjectDto stackResponse(Project project) {
        return ProjectDto.builder()
            .id(project.getId())
            .videoUrl(project.getVideoUrl())
            .title(project.getTitle())
            .description(project.getDescription())
            .bpm(project.getBpm())
            .codeFlow(project.getCodeFlow())
            .scope(project.getScope())
            .isStackable(project.getIsStackable())
            .viewCount(project.getViewCount())
            .prevProjectId(project.getPrevProjectId())
            .likeCount(project.getLikeCount())
            .stackCount(project.getStackCount())
            .build();
    }

    public static ProjectDto insertProject(String title, String description, String bpm, String codeFlow,
        List<Long> instrumentIds, Scope scope, Boolean isStackable) {
        return ProjectDto.builder()
            .title(title)
            .description(description)
            .bpm(bpm)
            .codeFlow(codeFlow)
            .instrumentIds(instrumentIds)
            .scope(scope)
            .isStackable(isStackable)
            .build();
    }

    // ProjectDto -> Project
    // (+) videoUrl, prevProjectId, User 추가적으로 필요
    public Project toEntity(String videoUrl, Long prevProjectId, User user) {
        return Project.builder()
            .videoUrl(videoUrl)
            .title(title)
            .description(description)
            .bpm(bpm)
            .codeFlow(codeFlow)
            .scope(scope)
            .isStackable(isStackable)
            .prevProjectId(prevProjectId)
            .user(user)
            .viewCount(0)
            .build();
    }
}
