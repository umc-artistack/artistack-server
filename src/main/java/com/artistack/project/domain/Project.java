package com.artistack.project.domain;

import com.artistack.config.BaseTimeEntity;
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

    private String description;

    private Boolean isStackable;

    private String scope;

    private String video_url;

    private int bpm;

    private String codeFlow;

    private Integer instrumentId;

    private int prevProjectId;



}
