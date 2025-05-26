

package com.diya.service;

import com.diya.dto.request.ForgotPasswordRequest;
import com.diya.dto.request.ResetPasswordRequest;
import com.diya.dto.response.MessageResponse;
import com.diya.exception.ResourceNotFoundException;
import com.diya.exception.TokenRefreshException;
import com.diya.model.PasswordResetToken;
import com.diya.model.User;
import com.diya.repository.PasswordResetTokenRepository;
import com.diya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public MessageResponse requestPasswordReset(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        String token = generateResetToken();

        // Delete any existing tokens for the user
        passwordResetTokenRepository.deleteByUser(user);

        // Create new token
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();

        passwordResetTokenRepository.save(resetToken);

        // Send email with password reset link
        emailService.sendPasswordResetEmail(user.getEmail(), token);

        return new MessageResponse("Password reset instructions have been sent to your email.");
    }

    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new TokenRefreshException(request.getToken(), "Invalid password reset token"));

        if (resetToken.isExpired()) {
            throw new TokenRefreshException(request.getToken(), "Password reset token is expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        return new MessageResponse("Password has been reset successfully.");
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }
}
