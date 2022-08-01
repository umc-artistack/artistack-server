package com.artistack.project.dto;

import com.artistack.instrument.domain.Instrument;
import com.artistack.instrument.dto.InstrumentDto;
import com.artistack.project.domain.Project;
import com.artistack.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectDto {
    private String title;
    private String description;

    private String bpm;

    private String codeFlow;

    private Integer instrument;

    private Integer scope;

    private Boolean isStackable;

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
