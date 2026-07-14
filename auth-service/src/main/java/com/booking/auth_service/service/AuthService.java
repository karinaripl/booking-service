package com.booking.auth_service.service;

import com.booking.auth_service.dto.AuthRequest;
import com.booking.auth_service.dto.AuthResponse;
import com.booking.auth_service.dto.RegisterRequest;
import com.booking.auth_service.exception.AuthException;
import com.booking.auth_service.repository.UserRepository;
import com.booking.model.generated.auth.tables.records.UsersRecord;
import org.jooq.DSLContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.booking.model.generated.auth.tables.Users.USERS;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request) {
        UsersRecord user = new UsersRecord();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(), "USER");
        return new AuthResponse(token);
    }

    public AuthResponse login(AuthRequest request) {
        UsersRecord user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new AuthException("Invalid password");
        }

        String token = jwtService.generateToken(user.getEmail(), "USER");
        return new AuthResponse(token);
    }
}