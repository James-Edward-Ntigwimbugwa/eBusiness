package tz.business.eCard.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import tz.business.eCard.userDetailService.UserDetailsImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

@Component
public class JWTutils {
    @Value("${drppt.co.tz.jwtkey}")
    private String jwtKey;

    Logger log = LoggerFactory.getLogger(JWTutils.class);

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder().setSubject(userPrincipal.getUsername())
                .claim("roles", new ArrayList<>())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(1)))
                .signWith(Keys.hmacShaKeyFor(jwtKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public  String getUsernameFromJwtToken(String token) {
        String username = Jwts.parser().setSigningKey(jwtKey).parseClaimsJws(token).getBody().getSubject();
        log.debug("username: {}", username);
        return username;
    }

    public String getIdFromJwtToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtKey).parseClaimsJws(token).getBody();
        String id = claims.getId();
        log.debug("id: {}", id);
        return id;
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtKey).parseClaimsJws(authToken);
            return true;
        }catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        }catch (MalformedJwtException e){
            log.warn("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e){
            log.warn("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e){
            log.warn("Unsupported JWT token: {}", e.getMessage());
        }catch (IllegalArgumentException e){
            log.warn("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
        return  false;
    }
}
