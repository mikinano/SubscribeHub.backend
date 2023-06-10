package com.subscribehub.app.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.function.Function;

@Service
@Transactional
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);

    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractTokenType(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("token_type", String.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        Claims claims = Jwts.claims();
        claims.put("token_type", "access");
        return buildToken(claims, userDetails, ACCESS_TOKEN_DURATION);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Claims claims = Jwts.claims();
        claims.put("token_type", "refresh");
        return buildToken(claims, userDetails, REFRESH_TOKEN_DURATION);
    }

    private String buildToken(
            Claims claims,
            UserDetails userDetails,
            Duration expiration
    ) {
        Date now = new Date();
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(now.getTime() + expiration.toMillis()))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Claims claims = extractAllClaims(token);
            String userName = claims.getSubject();

            return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) { //
            // 복호화 과정에서 에러가 나면 유효하지 않은 토큰
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}