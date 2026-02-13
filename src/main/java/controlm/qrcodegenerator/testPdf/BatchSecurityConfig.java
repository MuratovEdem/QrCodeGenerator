package controlm.qrcodegenerator.testPdf;

import controlm.qrcodegenerator.config.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchSecurityConfig {

    private final CustomUserDetailService userDetailsService;

    public void authenticateAsSystem() {
        UserDetails systemUser = userDetailsService.loadUserByUsername("admin");
        Authentication authentication = new UsernamePasswordAuthenticationToken(systemUser, null, systemUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public void clear() {
        SecurityContextHolder.clearContext();
    }
}
