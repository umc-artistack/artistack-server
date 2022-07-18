package com.artistack.project.domain;

import com.artistack.config.BaseTimeEntity;
import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
public class Project extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String description;

    @NotNull
    private Boolean isStackable;

    @NotNull
    private String scope;

    @NotNull
    private String videoUrl;

    private Integer bpm;

    private String codeFlow;

    @NotNull
    private Integer instrumentId;

    @NotNull
    private Long prevProjectId;

    @Builder
    public Project(String description, Boolean isStackable, String scope, String videoUrl, Integer bpm, String codeFlow, Integer instrumentId, Long prevProjectId) {
        this.description = description;
        this.isStackable = isStackable;
        this.scope = scope;
        this.videoUrl = videoUrl;
        this.bpm = bpm;
        this.codeFlow = codeFlow;
        this.instrumentId = instrumentId;
        this.prevProjectId = prevProjectId;
    }
}
