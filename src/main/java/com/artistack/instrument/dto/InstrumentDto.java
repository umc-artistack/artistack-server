package com.artistack.instrument.dto;

import com.artistack.instrument.domain.Instrument;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstrumentDto {

    private Long id;
    private String name;
    private String imgUrl;

    public static InstrumentDto response(Instrument instrument) {
        return InstrumentDto.builder()
            .id(instrument.getId())
            .name(instrument.getName())
            .imgUrl(instrument.getImgUrl())
            .build();
    }
}

