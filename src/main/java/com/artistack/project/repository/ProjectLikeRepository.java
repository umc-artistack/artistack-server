package com.artistack.project.repository;
import com.artistack.project.domain.ProjectLike;
import com.artistack.user.domain.User;
import com.artistack.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectLikeRepository extends JpaRepository<ProjectLike, Long> {

    List<ProjectLike> findByProject(Project project);

    Optional<ProjectLike> findByUserAndProject(User user, Project project);

    void deleteByUserAndProject(User user, Project project);

}
