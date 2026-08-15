package controlm.qrcodegenerator.config;

import controlm.qrcodegenerator.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class ForcePasswordChangeFilter extends OncePerRequestFilter {
    private final List<String> ALLOWED_PATHS = List.of(
            "/auth/change-password",
            "/auth/login",
            "/logout"
    );

    private final List<String> ALLOWED_PREFIXES = List.of(
            "/css/", "/js/", "/images/", "/static/", "/webjars/"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return ALLOWED_PATHS.contains(uri)
                || ALLOWED_PREFIXES.stream().anyMatch(uri::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof User user)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (user.isPasswordTemporary()) {
            log.info("ForcePasswordChangeFilter redirect /auth/change-password");
            response.sendRedirect(request.getContextPath() + "/auth/change-password");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
