package com.artistack.project.dto;
import com.artistack.project.domain.Project;
import com.artistack.user.domain.User;
import com.artistack.user.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ProjectDto {

    private Long id;
    private String videoUrl;
    private String title;
    private String description;
    private Boolean isStackable;
    private String scope;
    private String codeFlow;
    private Integer bpm;
    private Integer viewCount;
    private Long prevProjectId;
    private User user;


    public static ProjectDto response(Project project) {

        return ProjectDto.builder()
                .id(project.getId())
                .description(project.getDescription())
                .isStackable(project.getIsStackable())
                .scope(project.getScope())
                .videoUrl(project.getVideoUrl())
                .bpm(project.getBpm())
                .codeFlow(project.getCodeFlow())
                .viewCount(project.getViewCount())
                .prevProjectId(project.getPrevProjectId())
                .user(project.getUser())
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
                .viewCount(project.getViewCount())
                .prevProjectId(project.getPrevProjectId())
                .user(project.getUser())
                .build();
    }

    // ProjectDto -> Project
    // Request 정보 + videoUrl과 prevProjectId가 추가적으로 필요
    public Project toEntity(String videoUrl, Long prevProjectId) {
        return Project.builder()
                .videoUrl(videoUrl)
                .description(description)
                .bpm(bpm)
                .codeFlow(codeFlow)
                .scope(scope)
                .isStackable(isStackable)
                .prevProjectId(prevProjectId)
                .build();
    }
}