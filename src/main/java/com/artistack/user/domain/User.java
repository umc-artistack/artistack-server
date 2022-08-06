package com.artistack.user.domain;

import com.artistack.config.BaseTimeEntity;
import com.artistack.oauth.constant.ProviderType;
import com.artistack.user.constant.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnTransformer;

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
    @Setter
    private String nickname;

    @Column(nullable = true, length = 255)
    @ColumnTransformer(write="trim(?)")
    @Setter
    private String description;

    @Column(nullable = true)
    @Setter
    private String profileImgUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ProviderType providerType;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private Role role = Role.USER;

    @Builder
    public User(String artistackId, String nickname, String description, String profileImgUrl, ProviderType providerType) {
        this.artistackId = artistackId;
        this.nickname = nickname;
        this.description = description;
        this.profileImgUrl = profileImgUrl;
        this.providerType = providerType;
    }

    public void withdraw() {
        artistackId = null;
        nickname = null;
        description = null;
        profileImgUrl = null;
        role = Role.WITHDRAWAL;
    }
}
