package com.artistack.project.repository;

import com.artistack.project.domain.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    public Page<Project> findAll(Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.id =:id")
    public List<Project> findByProjectId(Long id);
}
