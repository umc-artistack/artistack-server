package com.artistack.project.domain;

import com.artistack.config.BaseTimeEntity;
import com.artistack.instrument.domain.ProjectInstrument;
import com.artistack.project.constant.Scope;
import com.artistack.user.domain.User;
import com.sun.istack.NotNull;
import java.util.List;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import org.hibernate.annotations.Formula;

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

    private String bpm;

    private String codeFlow;

    @OneToMany(mappedBy = "project")
    List<ProjectInstrument> instruments;

    @NotNull
    @Enumerated
    private Scope scope;

    @NotNull
    private Boolean isStackable;

    @NotNull
    private Long prevProjectId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Integer viewCount;

    // 메이슨) 현재 프로젝트를 스택한 프로젝트 개수
    @Formula("(SELECT count(*) FROM project p where p.prev_project_id = id)")
    private Integer stackCount;

    @Builder
    public Project(Long id, String videoUrl, String title, String description, String bpm, String codeFlow,
        List<ProjectInstrument> instruments, Scope scope, Boolean isStackable, Long prevProjectId, User user, Integer viewCount) {
        this.id = id;
        this.videoUrl = videoUrl;
        this.title = title;
        this.description = description;
        this.bpm = bpm;
        this.codeFlow = codeFlow;
        this.instruments = instruments;
        this.scope = scope;
        this.isStackable = isStackable;
        this.viewCount = viewCount;
        this.prevProjectId = prevProjectId;
        this.user = user;
    }
}