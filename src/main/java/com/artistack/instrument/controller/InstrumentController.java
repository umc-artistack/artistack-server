package com.artistack.instrument.controller;

import com.artistack.base.dto.DataResponseDto;
import com.artistack.instrument.service.InstrumentService;
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
public class InstrumentController {

    private final InstrumentService instrumentService;
    
    @GetMapping(path = "")
    public DataResponseDto<Object> get() {
        return DataResponseDto.of(instrumentService.getAll());
    }
}