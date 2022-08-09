package com.artistack.project.dto;

import com.artistack.instrument.domain.ProjectInstrument;
import com.artistack.instrument.dto.InstrumentDto;
import com.artistack.instrument.repository.ProjectInstrumentRepository;
import com.artistack.project.constant.Scope;
import com.artistack.project.domain.Project;
import com.artistack.user.domain.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

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

    private User user;

    private List<InstrumentDto> instruments;

    private Integer likeCount;

    private Integer stackCount;

    public ProjectDto() {

    }

    public static ProjectDto response(Project project) {
        return response(project, null);
    }

    public static ProjectDto response(Project project, ProjectInstrumentRepository projectInstrumentRepository) {
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
            .user(project.getUser())
            .instruments(Optional.ofNullable(projectInstrumentRepository).map(
                e -> e.findByProjectId(project.getId()).stream().map(ProjectInstrument::getInstrument)
                    .map(InstrumentDto::response).collect(Collectors.toList())).orElse(null))
            .stackCount(project.getStackCount())
            .likeCount(project.getLikeCount())
            .build();
    }

    // 메이슨
    // TODO: 좋아요 기능 구현 시 좋아요 수도 반환하도록 수정
    public static ProjectDto profileResponse(Project project) {
        return ProjectDto.builder()
            .id(project.getId())
            .videoUrl(project.getVideoUrl())
            .stackCount(project.getStackCount())
            .viewCount(project.getViewCount())
            .likeCount(null)
            .build();
    }

    public static ProjectDto getProject(Project project) {
        return getProject(project, null);
    }

    public static ProjectDto getProject(Project project, ProjectInstrumentRepository projectInstrumentRepository) {
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
            .user(project.getUser())
            .instruments(Optional.ofNullable(projectInstrumentRepository).map(
                e -> e.findByProjectId(project.getId()).stream().map(ProjectInstrument::getInstrument)
                    .map(InstrumentDto::response).collect(Collectors.toList())).orElse(null))
            .stackCount(project.getStackCount())
            .likeCount(project.getLikeCount())
            .build();

    }

    public static ProjectDto insertProject(String title, String description, String bpm, String codeFlow,
        List<InstrumentDto> instruments, Scope scope, Boolean isStackable) {
        return ProjectDto.builder()
            .title(title)
            .description(description)
            .bpm(bpm)
            .codeFlow(codeFlow)
            .instruments(instruments)
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
