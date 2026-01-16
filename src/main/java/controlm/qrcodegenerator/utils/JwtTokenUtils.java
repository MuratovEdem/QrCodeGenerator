//package controlm.qrcodegenerator.utils;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import java.nio.charset.StandardCharsets;
//import java.time.Duration;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Component
//public class JwtTokenUtils {
//    @Value("${jwt.secret}")
//    private String jwtAccessSecret;
//
//    @Value("${jwt.lifetimeAccessToken}")
//    private Duration jwtLifetimeAccessToken;
//
//    @Value("${jwt.lifetimeRefreshToken}")
//    private Duration jwtLifetimeRefreshToken;
//
//    public String generateAccessToken(UserDetails userDetails) {
//        List<String> rolesList = userDetails.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList());
//
//        Date issuedDate = new Date();
//        Date expiredDate = new Date(issuedDate.getTime() + jwtLifetimeAccessToken.toMillis());
//        return Jwts.builder()
//                .claims()
//                .subject(userDetails.getUsername())
//                .issuedAt(issuedDate)
//                .expiration(expiredDate)
//                .add("roles", rolesList)
//                .and()
//                .signWith(Keys.hmacShaKeyFor(jwtAccessSecret.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
//                .compact();
//    }
//
//    public String generateRefreshToken(UserDetails userDetails) {
//        Date issuedDate = new Date();
//        Date expiredDate = new Date(issuedDate.getTime() + jwtLifetimeRefreshToken.toMillis());
//        return Jwts.builder()
//                .claims()
//                .subject(userDetails.getUsername())
//                .issuedAt(issuedDate)
//                .expiration(expiredDate)
//                .and()
//                .signWith(Keys.hmacShaKeyFor(jwtAccessSecret.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
//                .compact();
//    }
//
//    public String getUsername(String token) {
//        return getAllClaimsFromToken(token).getSubject();
//    }
//
//    public List<String> getRoles(String token) {
//        Claims claims = getAllClaimsFromToken(token);
//        Object rolesClaim = claims.get("roles");
//
//        if (rolesClaim instanceof List<?> rolesList) {
//            return rolesList.stream()
//                    .map(Object::toString)
//                    .collect(Collectors.toList());
//        }
//        return Collections.emptyList();
//    }
//
//    private Claims getAllClaimsFromToken(String token) {
//        return Jwts.parser()
//                .verifyWith(Keys.hmacShaKeyFor(jwtAccessSecret.getBytes(StandardCharsets.UTF_8)))
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }
//}