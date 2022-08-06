package com.artistack.jwt.domain;

import com.artistack.config.BaseTimeEntity;
import com.artistack.user.domain.User;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "`token`")
public class Jwt extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String refreshToken;

    @OneToOne
    @JoinColumn
    private User user;

    @Builder
    public Jwt(User user, String refreshToken) {
        this.user = user;
        this.refreshToken = refreshToken;
    }
}
