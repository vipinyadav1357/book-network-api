package com.vipin.book.service;

import com.vipin.book.dtos.AuthenticationRequest;
import com.vipin.book.dtos.AuthenticationResponse;
import com.vipin.book.dtos.RegisterRequest;
import com.vipin.book.emailservice.EMailTemplateName;
import com.vipin.book.emailservice.EmailService;
import com.vipin.book.exceptionhandler.OperationNotPermittedException;
import com.vipin.book.model.Token;
import com.vipin.book.model.User;
import com.vipin.book.repo.RoleRepo;
import com.vipin.book.repo.TokenRepo;
import com.vipin.book.repo.UserRepo;
import com.vipin.book.security.JwtService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final TokenRepo tokenRepo;
    private final EmailService emailService;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private String activationUrl = "http://localhost:4200/activate-account";

    public void register(@NotNull RegisterRequest request) {
        var userRole = roleRepo.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initialize"));
        var user = User.builder().firstname(request.getFirstname()).lastname(request.getLastname())
                .email(request.getEmail()).password(encoder.encode(request.getPassword())).accountlocked(false)
                .enabled(false).roles(List.of(userRole)).build();
        userRepo.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) {
        var token = generateAndSaveActivationToken(user);
        try {
            emailService.sendEmail(user.getEmail(), user.fullName(), EMailTemplateName.ACTIVATE_ACCOUNT, activationUrl,
                    token, "Account Activation");
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send validation email", e); // Wrap it as unchecked
        }
    }

    private @NotNull String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder().toekn(generatedToken).createdat(LocalDateTime.now())
                .expiredat(LocalDateTime.now().plusMinutes(15)).user(user).build();
        tokenRepo.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String character = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(character.length());
            codeBuilder.append(character.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(@NotNull AuthenticationRequest request) {
        Authentication auth;
        try {
            auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException e) {
            throw new OperationNotPermittedException("Invalid Crediential hai bachcha");
        }
        var user = (User) auth.getPrincipal();
        var claims = new HashMap<String, Object>();
        claims.put("fullname", user.fullName());
        var jwtToken = jwtService.generateToken(claims, user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    @Transactional
    public void activateAccount(String token) {
        Token savedToken = tokenRepo.findByToekn(token).orElseThrow(() -> new RuntimeException("Invalid Token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiredat())) {
            sendValidationEmail(savedToken.getUser()); // Might throw MessagingException
            tokenRepo.delete(savedToken);
            new RuntimeException("Activation token has expired please fill the new generated");
        } else {
            var user = userRepo.findById(savedToken.getUser().getId())
                    .orElseThrow(() -> new UsernameNotFoundException("username not found"));
            user.setEnabled(true);
            userRepo.save(user);
            savedToken.setValidatedat(LocalDateTime.now());
            tokenRepo.save(savedToken);
        }
    }

}
