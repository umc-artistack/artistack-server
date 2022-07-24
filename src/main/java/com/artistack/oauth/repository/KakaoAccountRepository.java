package com.artistack.oauth.repository;

import com.artistack.oauth.domain.KakaoAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KakaoAccountRepository extends JpaRepository<KakaoAccount, String> {
}

