package io.bluextech.ordika.utils;
/* Created by limxuanhui on 12/8/24 */

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Deserializer;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Getter
@Component
public class JwtUtil {

    private final String JWT_SECRET;
    private final Deserializer<Map<String, ?>> deserializer;

    public JwtUtil(@Value("${ordika.auth.JWT_SECRET}") String JWT_SECRET) {
        this.JWT_SECRET = JWT_SECRET;
        this.deserializer = new JacksonDeserializer<>();
    }

    public SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public JwsHeader extractHeader(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getHeader();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @SuppressWarnings("unchecked")
    public <T> T extractCustomClaim(String token, String key) {
        Jwt<?, ?> parsedToken = Jwts.parser().json(deserializer)
                .verifyWith(getSecretKey())
                .build()
                .parse(token);

        Map<String, Object> parsedPayload = (Map<String, Object>) parsedToken.getPayload();
        return (T) parsedPayload.get(key);
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean isTokenValid(String token, String userId) {
        final String subject = extractSubject(token);
        return subject.equals(userId) && !isTokenExpired(token);
    }

    public String createToken(String subject, Map<String, Object> claims, Long tokenValidityMs) {
        return Jwts.builder()
                .header().type("JWT").and()
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenValidityMs))
                .claims().add(claims).and()
                .signWith(getSecretKey())
                .compact();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(getSecretKey()).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

}
