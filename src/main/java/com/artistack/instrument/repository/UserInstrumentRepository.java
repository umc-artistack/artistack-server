package com.artistack.instrument.repository;

import com.artistack.instrument.domain.UserInstrument;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInstrumentRepository extends JpaRepository<UserInstrument, Long> {
    List<UserInstrument> findByUserId(Long userId);
}
