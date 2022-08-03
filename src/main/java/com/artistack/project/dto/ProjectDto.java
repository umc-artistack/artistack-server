package com.artistack.project.dto;

import com.artistack.instrument.domain.ProjectInstrument;
import com.artistack.instrument.dto.InstrumentDto;
import com.artistack.instrument.repository.ProjectInstrumentRepository;
import com.artistack.project.domain.Project;
import com.artistack.user.domain.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
public class ProjectDto {
    private Long id;
    private String videoUrl;
    private String title;
    private String description;

    private String bpm;

    private String codeFlow;

    private Integer instrument;

    private Integer scope;

    private Boolean isStackable;

    private Integer viewCount;

    private Long prevProjectId;

    private User user;

    private List<InstrumentDto> instruments;

    private Integer likeCount;

    private Integer stackCount;

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
            .build();
    }

    public static ProjectDto getProject(Project project) {
        return response(project, null);
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
            .build();

    }

    public ProjectDto(String title, String description, String bpm, String codeFlow, Integer instrument,
        Integer scope, Boolean isStackable) {
        this.title = title;
        this.description = description;
        this.bpm = bpm;
        this.codeFlow = codeFlow;
        this.instrument = instrument;
        this.scope = scope;
        this.isStackable = isStackable;
    }

    // ProjectDto -> Project
    // (+) videoUrl, prevProjectId, User 추가적으로 필요
    public Project toEntity(String videoUrl, Long prevProjectId, InstrumentDto instrumentDto, User user) {
        return Project.builder()
                .videoUrl(videoUrl)
                .title(title)
                .description(description)
                .bpm(bpm)
                .codeFlow(codeFlow)
                .instrument(instrumentDto.toEntity())
                .scope(scope)
                .isStackable(isStackable)
                .prevProjectId(prevProjectId)
                .user(user)
                .build();
    }
}