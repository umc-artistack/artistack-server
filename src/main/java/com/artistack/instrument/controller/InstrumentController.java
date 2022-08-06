package com.artistack.instrument.controller;

import com.artistack.base.dto.DataResponseDto;
import com.artistack.instrument.service.InstrumentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/instruments")
@Slf4j
@Api(tags = "악기 관련 API ")
public class InstrumentController {

    private final InstrumentService instrumentService;

    @ApiOperation(
        value = "악기 정보 조회", notes = "DB에 저장되어 있는 악기들의 id, 이름, 이미지 URL을 확인할 수 있습니다.")
    @GetMapping(path = "")
    public DataResponseDto<Object> get() {
        return DataResponseDto.of(instrumentService.getAll());
    }
}