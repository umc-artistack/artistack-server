package com.artistack.instrument.domain;

import com.artistack.config.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "`instrument`")
public class Instrument extends BaseTimeEntity {
    @Id
    @Column
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String imgUrl;

    @Builder
    public Instrument(Long id, String name, String imgUrl) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
    }
}
