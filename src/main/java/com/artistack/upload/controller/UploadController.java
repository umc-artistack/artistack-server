package com.artistack.upload.controller;

import com.artistack.base.dto.DataResponseDto;
import com.artistack.upload.dto.UploadDto;
import com.artistack.upload.service.UploadService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/upload")
@Slf4j
public class UploadController {

    private final UploadService uploadService;

    /**
     * 메이슨) 이미지를 업로드합니다.
     *
     * @param uploadDto 업로드 이미지 body
     * @param path      업로드할 경로
     * @param multiple  업로드할 이미지가 여러개인지 여부
     * @return
     */
    @PostMapping({"{path}", ""})
    public DataResponseDto<Object> upload(
        @ModelAttribute UploadDto uploadDto,
        @PathVariable(value = "path", required = false) Optional<String> path,
        @RequestParam(value = "multiple") Boolean multiple
    ) {
        return DataResponseDto.of(multiple ? uploadService.uploadFiles(uploadDto, path)
            : uploadService.uploadFile(uploadDto.getFile(), path));
    }
}