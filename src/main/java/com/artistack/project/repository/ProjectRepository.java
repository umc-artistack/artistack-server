package com.artistack.project.repository;

import com.artistack.project.domain.Project;
import com.artistack.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findAll(Pageable pageable);
    Optional<Project> findById(Long id);
    
    // TODO: Optional로 수정할 것!
    @Query(value = "select p.isStackable from Project p where p.id = ?1")
    Boolean findStackableById(Long id);

    List<Project> findAllByPrevProjectId(Long id);

    Optional<Project> findProjectByPrevProjectId(Long id);

    // 메이슨
    // TODO: status가 공개(public)인 프로젝트만 조회 가능하게 수정
    @Query(value = "SELECT p FROM Project p WHERE (:userId IS NULL OR p.user.id = :userId)")
    Page<Project> getByConditionWithPaging(Pageable pageable, Long userId);

}
