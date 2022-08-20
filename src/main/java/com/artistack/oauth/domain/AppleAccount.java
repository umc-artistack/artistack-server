package com.artistack.oauth.domain;

import com.artistack.config.BaseTimeEntity;
import com.artistack.user.domain.User;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "`apple_account`")
public class AppleAccount extends BaseTimeEntity {

    @Id
    @Column
    private String id;

    @Column(nullable = false)
    @Setter
    private String refreshToken;

    @OneToOne
    @JoinColumn
    private User user;

    @Builder
    public AppleAccount(String id, String refreshToken, User user) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.user = user;
    }
}
