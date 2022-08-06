package com.artistack.oauth.repository;

import com.artistack.oauth.domain.KakaoAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KakaoAccountRepository extends JpaRepository<KakaoAccount, String> {

    Optional<KakaoAccount> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}

