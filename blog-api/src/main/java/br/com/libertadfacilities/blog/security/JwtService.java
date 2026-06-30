package br.com.libertadfacilities.blog.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSignInKey())
                .compact();
    }

    public String extractUsername(String token) throws JwtException {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        try {
            return !extractClaim(token, Claims::getExpiration).before(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    public String generateTemporaryTwoFactorToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 5 * 60 * 1000);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("type", "2fa")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTemporaryTwoFactorToken(String token) {
        String type = extractAllClaims(token).get("type", String.class);
        return "2fa".equals(type) && isTokenExpired(token);
    }

    public String extractUsernameFromAnyToken(String token) {
        return extractUsername(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) throws JwtException {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}