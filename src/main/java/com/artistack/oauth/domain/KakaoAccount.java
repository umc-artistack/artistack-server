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
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "`kakao_account`")
@ToString(of = {"id", "user"})
public class KakaoAccount extends BaseTimeEntity {

    @Id
    @Column
    private String id;

    @OneToOne
    @JoinColumn
    private User user;

    @Builder
    public KakaoAccount(String id, User user) {
        this.id = id;
        this.user = user;
    }
}
