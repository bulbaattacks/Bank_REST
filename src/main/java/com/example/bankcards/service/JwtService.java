package com.example.bankcards.service;

import com.example.bankcards.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-expiration}")
    private long jwtRefreshExpiration;

    public String generateToken(User user) {
        Map<String, ?> claims = Map.of(
                "userId", user.getId(),
                "role", user.getRole()
        );
        return buildToken(claims, user, jwtExpiration);
    }

    public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), user, jwtRefreshExpiration);
    }

    private String buildToken(Map<String, ?> extraClaims, User user, long expiration) {
        var currentTime = System.currentTimeMillis();
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(user.getLogin())
                .issuedAt(new Date(currentTime))
                .expiration(new Date(currentTime + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
