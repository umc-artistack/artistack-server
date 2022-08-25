package com.artistack.instrument.repository;

import com.artistack.instrument.domain.ProjectInstrument;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectInstrumentRepository extends JpaRepository<ProjectInstrument, Long> {

    List<ProjectInstrument> findByProjectId(Long projectId);

    void deleteByProjectId(Long projectId);
}
