//package controlm.qrcodegenerator.auth.service;
//
//import controlm.qrcodegenerator.auth.dto.JwtRequest;
//import controlm.qrcodegenerator.auth.dto.JwtResponse;
//import controlm.qrcodegenerator.dto.request.RegistrationUserDto;
//import controlm.qrcodegenerator.dto.response.UserResponseDto;
//import controlm.qrcodegenerator.enums.RoleEnum;
//import controlm.qrcodegenerator.exception.IncorrectUsernameException;
//import controlm.qrcodegenerator.exception.JwtTokenException;
//import controlm.qrcodegenerator.exception.NotFoundException;
//import controlm.qrcodegenerator.mapper.UserMapper;
//import controlm.qrcodegenerator.model.User;
//import controlm.qrcodegenerator.service.UserService;
//import controlm.qrcodegenerator.utils.JwtTokenUtils;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.MalformedJwtException;
//import io.jsonwebtoken.UnsupportedJwtException;
//import io.jsonwebtoken.security.SignatureException;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//
//    private final Map<String, String> refreshStorage = new HashMap<>();
//    private final UserService userService;
//    private final JwtTokenUtils jwtTokenUtils;
//    private final AuthenticationManager authenticationManager;
//
//    public JwtResponse createAuthToken(JwtRequest authRequest) {
//        Authentication authenticate = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
//        UserDetails userDetails = (UserDetails) authenticate.getPrincipal();
//
//        String accessToken = jwtTokenUtils.generateAccessToken(userDetails);
//        String refreshToken = jwtTokenUtils.generateRefreshToken(userDetails);
//
//        refreshStorage.put(userDetails.getUsername(), refreshToken);
//
//        return new JwtResponse(accessToken, refreshToken);
//    }
//
//    public JwtResponse refreshToken(HttpServletRequest request) {
//        String authHeader = request.getHeader("Authorization");
//        String refreshToken = null;
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            refreshToken = authHeader.substring(7);
//        }
//
//        try {
//            String username = jwtTokenUtils.getUsername(refreshToken);
//            String savedRefreshToken = refreshStorage.get(username);
//
//            if (savedRefreshToken != null && savedRefreshToken.equals(refreshToken)) {
//                User user = userService.findByUsername(username)
//                        .orElseThrow(() -> new NotFoundException("User with username = " + username + " not found"));
//                String accessToken = jwtTokenUtils.generateAccessToken(user);
//                String newRefreshToken = jwtTokenUtils.generateRefreshToken(user);
//
//                refreshStorage.put(user.getUsername(), newRefreshToken);
//
//                return new JwtResponse(accessToken, newRefreshToken);
//            }
//        } catch (ExpiredJwtException ex) {
//            log.error("Token expired: {}", ex.getMessage());
//            throw new JwtTokenException(ex.getMessage());
//        } catch (UnsupportedJwtException ex) {
//            log.error("Unsupported JWT: {}", ex.getMessage());
//            throw new JwtTokenException(ex.getMessage());
//        } catch (MalformedJwtException ex) {
//            log.error("Malformed JWT: {}", ex.getMessage());
//            throw new JwtTokenException(ex.getMessage());
//        } catch (SignatureException ex) {
//            log.error("Invalid signature: {}", ex.getMessage());
//            throw new JwtTokenException(ex.getMessage());
//        } catch (IllegalArgumentException ex) {
//            log.error("JWT claims string is empty: {}", ex.getMessage());
//            throw new JwtTokenException(ex.getMessage());
//        }
//        return new JwtResponse(null, null);
//    }
//
//    public UserResponseDto getUser(String username) {
//        User user = userService.findByUsername(username)
//                .orElseThrow(() -> new NotFoundException("User with username = " + username + " not found"));
//        log.info("user = {}", user);
//        UserMapper userMapper = new UserMapper();
//        return userMapper.userToDto(user);
//    }
//
//    @Transactional
//    public UserResponseDto createNewUser(RegistrationUserDto userRegistrationRequestDto) {
//        if (userService.findByUsername(userRegistrationRequestDto.getUsername()).isPresent()) {
//            throw new IncorrectUsernameException("User with the specified name already exists");
//        }
//        User user = userService.create(userRegistrationRequestDto);
//        UserMapper userMapper = new UserMapper();
//        return userMapper.userToDto(user);
//    }
//}