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
    private String videoUrl;

    @NotNull
    private String title;

    @NotNull
    private String description;

    private Integer bpm;

    private String codeFlow;

    @NotNull
    private Integer instrumentId;

    @NotNull
    private Integer scope;

    @NotNull
    private Boolean isStackable;

    @NotNull
    private Long prevProjectId;

    @Builder
    public Project(String videoUrl, String title, String description, Integer bpm, String codeFlow, Integer instrumentId, Integer scope, Boolean isStackable, Long prevProjectId) {
        this.videoUrl = videoUrl;
        this.title = title;
        this.description = description;
        this.bpm = bpm;
        this.codeFlow = codeFlow;
        this.instrumentId = instrumentId;
        this.scope = scope;
        this.isStackable = isStackable;
        this.prevProjectId = prevProjectId;
    }
}
