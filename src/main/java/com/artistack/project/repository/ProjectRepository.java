package com.artistack.project.repository;

import com.artistack.project.constant.Scope;
import com.artistack.project.domain.Project;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findById(Long id);

    // TODO: Optional로 수정할 것!
    @Query(value = "select p.isStackable from Project p where p.id = ?1")
    Boolean findStackableById(Long id);

    List<Project> findAllByPrevProjectId(Long id);

    Project findTopByOrderByIdDesc();

    // 메이슨
    @Query(value = "SELECT p FROM Project p WHERE (:artistackId IS NULL OR p.user.artistackId = :artistackId) AND (:lastId IS NULL OR p.id < :lastId) AND p.scope = :scope")
    Page<Project> getByConditionWithPaging(Pageable pageable, String artistackId, Long lastId, Scope scope);

    // 메이슨
    @Query(value = "SELECT COUNT(p) FROM Project p WHERE p.user.artistackId = :artistackId AND p.scope = 'PUBLIC'")
    Long countPublicByArtistackId(String artistackId);
}
