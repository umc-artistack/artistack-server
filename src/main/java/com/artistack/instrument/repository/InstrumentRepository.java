package com.artistack.instrument.repository;

import com.artistack.instrument.domain.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstrumentRepository extends JpaRepository<Instrument, Long> {
}
