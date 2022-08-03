package com.artistack.instrument.dto;

import com.artistack.instrument.domain.Instrument;
import com.artistack.instrument.domain.ProjectInstrument;
import com.artistack.project.domain.Project;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstrumentDto {

    private Long id;
    private String name;
    private String imgUrl;

    public InstrumentDto() {
    }

    public static InstrumentDto response(Instrument instrument) {
        return InstrumentDto.builder()
            .id(instrument.getId())
            .name(instrument.getName())
            .imgUrl(instrument.getImgUrl())
            .build();
    }

    public Instrument toEntity() {
        return Instrument.builder()
            .id(id)
            .name(name)
            .imgUrl(imgUrl)
            .build();
    }

    public ProjectInstrument toEntity(Project project, Instrument instrument) {
        return ProjectInstrument.builder()
            .project(project)
            .instrument(instrument)
            .build();
    }
}

