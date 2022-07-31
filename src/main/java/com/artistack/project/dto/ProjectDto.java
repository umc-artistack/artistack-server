package com.artistack.project.dto;
import com.artistack.project.domain.Project;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectDto {

    private Long id;
    private String videoUrl;
    private String title;
    private String description;
    private Boolean isStackable;
    private String scope;
    private String codeFlow;
    private Integer bpm;
    private Long prevProjectId;
    private Long userId;


    public static ProjectDto response(Project project) {

        return ProjectDto.builder()
                .id(project.getId())
                .description(project.getDescription())
                .isStackable(project.getIsStackable())
                .scope(project.getScope())
                .videoUrl(project.getVideoUrl())
                .bpm(project.getBpm())
                .codeFlow(project.getCodeFlow())
                .prevProjectId(project.getPrevProjectId())
                .userId(project.getUserId())
                .build();
    }

    public static ProjectDto getProject(Project project) {

        return ProjectDto.builder()
                .id(project.getId())
                .description(project.getDescription())
                .isStackable(project.getIsStackable())
                .scope(project.getScope())
                .videoUrl(project.getVideoUrl())
                .bpm(project.getBpm())
                .codeFlow(project.getCodeFlow())
                .prevProjectId(project.getPrevProjectId())
                .userId(project.getUserId())
                .build();
    }
}