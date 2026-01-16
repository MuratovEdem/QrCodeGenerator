//package controlm.qrcodegenerator.config;
//
//import controlm.qrcodegenerator.utils.JwtTokenUtils;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.MalformedJwtException;
//import io.jsonwebtoken.UnsupportedJwtException;
//import io.jsonwebtoken.security.SignatureException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.stream.Collectors;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class JwtRequestFilter extends OncePerRequestFilter {
//    private final JwtTokenUtils jwtTokenUtils;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        String authHeader = request.getHeader("Authorization");
//        String username = null;
//        String jwt = null;
//        try {
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                jwt = authHeader.substring(7);
//                username = jwtTokenUtils.getUsername(jwt);
//            }
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
//                        username,
//                        null,
//                        jwtTokenUtils.getRoles(jwt).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
//                );
//                SecurityContextHolder.getContext().setAuthentication(token);
//            }
//            filterChain.doFilter(request, response);
//        } catch (ExpiredJwtException ex) {
//            log.error("Token expired: {}", ex.getMessage());
//            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token expired");
//        } catch (UnsupportedJwtException ex) {
//            log.error("Unsupported JWT: {}", ex.getMessage());
//            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unsupported JWT");
//        } catch (MalformedJwtException ex) {
//            log.error("Malformed JWT: {}", ex.getMessage());
//            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Malformed JWT");
//        } catch (SignatureException ex) {
//            log.error("Invalid signature: {}", ex.getMessage());
//            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid signature");
//        } catch (IllegalArgumentException ex) {
//            log.error("JWT claims string is empty: {}", ex.getMessage());
//            response.sendError(HttpStatus.UNAUTHORIZED.value(), "JWT claims string is empty");
//        }
//    }
//}
