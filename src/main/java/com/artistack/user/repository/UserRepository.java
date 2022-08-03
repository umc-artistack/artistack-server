package com.artistack.user.repository;

import com.artistack.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByArtistackId(String artistackId);
    Optional<User> findById(Long id);
}
