package com.artistack.jwt;

import com.artistack.base.GeneralException;
import com.artistack.base.constant.Code;
import com.artistack.jwt.dto.JwtDto;
import com.artistack.user.constant.Role;
import com.artistack.user.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 3;            // 3분
    private static final long ADMIN_ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 30L;  // 30일

    private final Key key;

    public TokenProvider(@Value("${jwt.secret-key}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtDto generateJwt(User user) {

        long now = (new Date()).getTime();

        Date accessTokenExpiresIn = new Date(
            now + (user.getRole().equals(Role.ADMIN) ? ADMIN_ACCESS_TOKEN_EXPIRE_TIME : ACCESS_TOKEN_EXPIRE_TIME));

        String accessToken = Jwts.builder()
            .setSubject(user.getId().toString())
            .claim("artistackId", user.getArtistackId())
            .claim("nickname", user.getNickname())
            .claim("providerType", user.getProviderType())
            .claim("role", user.getRole())
            .claim(AUTHORITIES_KEY, user.getRole())
            .setExpiration(accessTokenExpiresIn)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();

        // Refresh Token 생성
        Date refreshTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
        String refreshToken = Jwts.builder()
            .setExpiration(refreshTokenExpiresIn)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();

        return JwtDto.builder()
            .grantType(BEARER_TYPE)
            .accessToken(accessToken)
            .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
            .refreshToken(refreshToken)
            .build();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
            Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = new org.springframework.security.core.userdetails.User(claims.getSubject(), "",
            authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
//            throw new GeneralException(Code.MALFORMED_JWT);
        } catch (ExpiredJwtException e) {
//            throw new GeneralException(Code.EXPIRED_ACCESS_TOKEN);
        } catch (UnsupportedJwtException e) {
//            throw new GeneralException(Code.UNSUPPORTED_JWT);
        } catch (IllegalArgumentException e) {
//            throw new GeneralException(Code.ILLEGAL_JWT);
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}