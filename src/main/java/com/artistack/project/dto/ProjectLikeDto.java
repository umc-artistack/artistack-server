package com.artistack.project.dto;

import com.artistack.instrument.domain.ProjectInstrument;
import com.artistack.instrument.dto.InstrumentDto;
import com.artistack.instrument.repository.ProjectInstrumentRepository;
import com.artistack.project.constant.Scope;
import com.artistack.project.domain.Project;
import com.artistack.project.domain.ProjectLike;
import com.artistack.user.domain.User;
import com.artistack.user.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
@AllArgsConstructor
public class ProjectLikeDto {

    private ProjectDto project;

    private UserDto user;

    public ProjectLikeDto() {

    }

    public static ProjectLikeDto projectLikeUsersResponse(ProjectLike projectLike) {
        return ProjectLikeDto.builder()
                .user(UserDto.previewResponse(projectLike.getUser()))
                .build();
    }


}
