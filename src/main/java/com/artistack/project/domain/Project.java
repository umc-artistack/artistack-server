package com.artistack.project.domain;

import com.artistack.config.BaseTimeEntity;
import com.artistack.instrument.domain.Instrument;
import com.artistack.user.domain.User;
import com.sun.istack.NotNull;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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

    private String bpm;

    private String codeFlow;

    @OneToOne
    @JoinColumn(name = "instrument_id")
    private Instrument instrument;

    @NotNull
    private Integer scope;

    @NotNull
    private Boolean isStackable;

    @NotNull
    private Long prevProjectId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Integer viewCount;

    @Builder
    public Project(Long id, String videoUrl, String title, String description, String bpm, String codeFlow,
        Instrument instrument, Integer scope, Boolean isStackable, Long prevProjectId, User user, Integer viewCount) {
        this.id = id;
        this.videoUrl = videoUrl;
        this.title = title;
        this.description = description;
        this.bpm = bpm;
        this.codeFlow = codeFlow;
        this.instrument = instrument;
        this.scope = scope;
        this.isStackable = isStackable;
        this.viewCount = viewCount;
        this.prevProjectId = prevProjectId;
        this.user = user;
    }
}