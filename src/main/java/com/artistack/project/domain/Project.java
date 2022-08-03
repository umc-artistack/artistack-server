package com.artistack.project.domain;

import com.artistack.config.BaseTimeEntity;
import com.artistack.user.domain.User;
import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    private Integer viewCount;

    @NotNull
    private Long prevProjectId;

    @ManyToOne
    @JoinColumn
    private User user;

    @Builder
    public Project(Long id, String videoUrl, String title, String description, Boolean isStackable, String scope, String codeFlow, Integer bpm, Integer viewCount, Long prevProjectId, User user) {
        this.id = id;
        this.videoUrl = videoUrl;
        this.title = title;
        this.description = description;
        this.isStackable = isStackable;
        this.scope = scope;
        this.codeFlow = codeFlow;
        this.bpm = bpm;
        this.viewCount = viewCount;
        this.prevProjectId = prevProjectId;
        this.user = user;
    }
}