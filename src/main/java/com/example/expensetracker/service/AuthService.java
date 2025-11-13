package com.example.expensetracker.service;

import com.example.expensetracker.config.JwtTokenProvider;
import com.example.expensetracker.dto.request.LoginRequest;
import com.example.expensetracker.dto.request.RegisterRequest;
import com.example.expensetracker.dto.response.AuthResponse;
import com.example.expensetracker.entity.User;
import com.example.expensetracker.entity.UserStatus;
import com.example.expensetracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwt;

    @Transactional
    public void register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.email()))
            throw new IllegalArgumentException("Email already in use");

        User u = User.builder()
                .email(req.email())
                .passwordHash(encoder.encode(req.password()))
                .fullName(req.fullName())
                .status(UserStatus.ACTIVE)
                .build();
        userRepo.save(u);
    }

    public AuthResponse login(LoginRequest req) {
        // Xác thực email/password (nếu sai sẽ ném BadCredentialsException)
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        User u = userRepo.findByEmail(req.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwt.generateToken(u.getEmail());
        return new AuthResponse(token, u.getEmail(), u.getFullName());
    }
}
