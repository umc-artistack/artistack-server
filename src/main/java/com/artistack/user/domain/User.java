package com.artistack.user.domain;

import com.artistack.config.BaseTimeEntity;
import com.artistack.instrument.domain.Instrument;
import com.artistack.oauth.constant.ProviderType;
import com.artistack.user.constant.Role;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "`user`")
@ToString(of = {"id", "artistackId", "description", "providerType"})
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, unique = true)
    private String artistackId;

    @Column(nullable = true)
    private String nickname;

    @Column(nullable = true, unique = true, length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ProviderType providerType;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Builder
    public User(String artistackId, String nickname, String description, ProviderType providerType) {
        this.artistackId = artistackId;
        this.nickname = nickname;
        this.description = description;
        this.providerType = providerType;
    }
}
