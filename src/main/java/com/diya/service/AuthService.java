
package com.diya.service;


import com.diya.dto.request.LoginRequest;
import com.diya.dto.request.RefreshTokenRequest;
import com.diya.dto.request.RegisterRequest;
import com.diya.dto.response.JwtResponse;
import com.diya.dto.response.MessageResponse;
import com.diya.exception.TokenRefreshException;
import com.diya.model.Cart;
import com.diya.model.RefreshToken;
import com.diya.model.User;
import com.diya.repository.UserRepository;
import com.diya.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        
        User userDetails = (User) authentication.getPrincipal();
        
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());
        
        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .id(userDetails.getId())
                .email(userDetails.getEmail())
                .role(userDetails.getRole().name())
                .firstName(userDetails.getFirstName())
                .lastName(userDetails.getLastName())
                .build();
    }
    
    @Transactional
    public JwtResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtTokenProvider.createAccessTokenFromUsername(user.getUsername());
                    
                    return JwtResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(requestRefreshToken)
                            .tokenType("Bearer")
                            .id(user.getId())
                            .email(user.getUsername())
                            .role(user.getRole().name())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .build();
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not found in database!"));
    }
    
    @Transactional
    public MessageResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
        
        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .role(User.Role.CUSTOMER)
                .build();
        
        // Create and link a cart
        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);
        
        userRepository.save(user);
        
        return new MessageResponse("User registered successfully!");
    }
    
    @Transactional
    public MessageResponse logout(RefreshTokenRequest request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
                .map(token -> {
                    refreshTokenService.revokeAllUserTokens(token.getUser());
                    return new MessageResponse("Logout successful!");
                })
                .orElseThrow(() -> new TokenRefreshException(request.getRefreshToken(),
                        "Refresh token is not found in database!"));
    }
}
