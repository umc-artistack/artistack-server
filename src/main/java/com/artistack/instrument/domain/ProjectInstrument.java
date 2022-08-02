package com.artistack.instrument.domain;

import com.artistack.config.BaseTimeEntity;
import com.artistack.project.domain.Project;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "`project_instrument`")
public class ProjectInstrument extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn
    private Project project;

    @ManyToOne
    @JoinColumn
    private Instrument instrument;


    @Builder
    public ProjectInstrument(Project project, Instrument instrument) {
        this.project = project;
        this.instrument = instrument;
    }
}
