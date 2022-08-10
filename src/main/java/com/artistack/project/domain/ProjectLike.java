package com.artistack.project.domain;

import com.artistack.config.BaseTimeEntity;

import javax.persistence.*;
import com.artistack.project.domain.Project;
import com.artistack.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "`project_like`")
public class ProjectLike extends BaseTimeEntity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder
    public ProjectLike( User user, Project project) {
        this.user = user;
        this.project = project;

    }

    public static ProjectLike of(User user, Project project) {
        return new ProjectLike(user, project);
    }
}
