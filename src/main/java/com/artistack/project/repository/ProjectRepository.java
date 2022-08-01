package com.artistack.project.repository;

import com.artistack.project.domain.Project;
import com.artistack.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    // TODO: Optional로 수정할 것!
    @Query(value = "select p.isStackable from Project p where p.id = ?1")
    Boolean findStackableById(Long id);

    List<Project> findAllByPrevProjectId(Long id);

    Optional<Project> findProjectByPrevProjectId(Long id);
}
