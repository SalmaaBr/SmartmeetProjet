package tn.esprit.examen.Smartmeet.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tn.esprit.examen.Smartmeet.security.services.UserDetailsImpl;


import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${Smartmeet.app.jwtSecret}")
  private String jwtSecret;

  @Value("${Smartmeet.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  public String generateJwtToken(Authentication authentication) {

    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

    return Jwts.builder()
            .setSubject((userPrincipal.getUsername()))
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
            .signWith(key(), SignatureAlgorithm.HS256)
            .compact();
  }

  private Key key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }

  public String getUserNameFromJwtToken(String token) {
    return Jwts.parser().setSigningKey(key()).build()
            .parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      Jws<Claims> claims = Jwts.parser()
              .setSigningKey(key())
              .build()
              .parseClaimsJws(authToken);
      
      // Check if token is expired
      if (claims.getBody().getExpiration().before(new Date())) {
        logger.error("JWT token is expired");
        return false;
      }
      
      // Check if token is blacklisted
      if (isTokenBlacklisted(authToken)) {
        logger.error("JWT token is blacklisted");
        return false;
      }
      
      return true;
    } catch (SignatureException e) {
      logger.error("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }
    return false;
  }

  private boolean isTokenBlacklisted(String token) {
    return false;
  }
  public String getJwtFromHeader(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    return null;
  }
}
