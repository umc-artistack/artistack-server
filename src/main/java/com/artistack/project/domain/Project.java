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

    @NotNull
    private Boolean isStackable;

    @NotNull
    private String scope;

    private String codeFlow;

    private Integer bpm;

    private Integer veiwCount;

    @NotNull
    private Long prevProjectId;

    @NotNull
    private Long userId;

    @Builder
    public Project(String videoUrl, String title, String description, Boolean isStackable, String scope, String codeFlow, Integer bpm, Long prevProjectId, Long userId) {
        this.videoUrl = videoUrl;
        this.title = title;
        this.description = description;
        this.isStackable = isStackable;
        this.scope = scope;
        this.codeFlow = codeFlow;
        this.bpm = bpm;
        this.prevProjectId = prevProjectId;
        this.userId = userId;
    }
}
