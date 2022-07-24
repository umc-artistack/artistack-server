package com.artistack.user.domain;

import com.artistack.config.BaseTimeEntity;
import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "`user`")
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String artistackId;

    @NotNull
    private String description;

    private Integer instrumentId;

//    @Enumerated(EnumType.STRING)
    @NotNull
    private String providerType; // TODO: String -> ProviderType로 타입 변경

    @NotNull
    private String status;

    @Builder
    public User(String artistackId, String description, Integer instrumentId, String providerType, String status) {
        this.artistackId = artistackId;
        this.description = description;
        this.instrumentId = instrumentId;
        this.providerType = providerType;
        this.status = status;
    }
}
