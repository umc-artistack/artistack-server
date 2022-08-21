package com.artistack.oauth.repository;

import com.artistack.oauth.domain.AppleAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppleAccountRepository extends JpaRepository<AppleAccount, String> {

    Optional<AppleAccount> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}

