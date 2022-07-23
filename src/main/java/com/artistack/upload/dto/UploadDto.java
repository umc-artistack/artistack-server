package com.artistack.upload.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadDto {

    List<String> urls;
    List<MultipartFile> files;
    MultipartFile file;
}
