package com.artistack.instrument.domain;

import com.artistack.config.BaseTimeEntity;
import com.artistack.user.domain.User;
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
@Table(name = "`user_instrument`")
public class UserInstrument extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn
    private User user;

    @ManyToOne
    @JoinColumn
    private Instrument instrument;


    @Builder
    public UserInstrument(User user, Instrument instrument) {
        this.user = user;
        this.instrument = instrument;
    }
}
