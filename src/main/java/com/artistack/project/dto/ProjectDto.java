package com.artistack.project.dto;

import com.artistack.project.domain.Project;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectDto {
    private String title;
    private String description;

    private Integer bpm;

    private String codeFlow;

    private Integer instrumentId;

    private Integer scope;

    private Boolean isStackable;

    @Builder
    public ProjectDto(String title, String description, Integer bpm, String codeFlow, Integer instrumentId,
        Integer scope, Boolean isStackable) {
        this.title = title;
        this.description = description;
        this.bpm = bpm;
        this.codeFlow = codeFlow;
        this.instrumentId = instrumentId;
        this.scope = scope;
        this.isStackable = isStackable;
    }

    // ProjectDto -> Project
    // Request 정보 + videoUrl과 prevProjectId가 추가적으로 필요
    public Project toEntity(String videoUrl, Long prevProjectId) {
        return Project.builder()
                .videoUrl(videoUrl)
                .description(description)
                .bpm(bpm)
                .codeFlow(codeFlow)
                .instrumentId(instrumentId)
                .scope(scope)
                .isStackable(isStackable)
                .prevProjectId(prevProjectId)
                .build();
    }
}
