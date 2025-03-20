package com.linkedIn.backend.features.authentication.service;

import com.linkedIn.backend.features.authentication.dto.AuthenticationRequestBody;
import com.linkedIn.backend.features.authentication.dto.AuthenticationResponseBody;
import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import com.linkedIn.backend.features.authentication.repository.AuthenticaltionUserRepository;
import com.linkedIn.backend.features.authentication.utils.EmailService;
import com.linkedIn.backend.features.authentication.utils.Encoder;
import com.linkedIn.backend.features.authentication.utils.JsonWebToken;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationService {
    private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final AuthenticaltionUserRepository authenticatedUserRepository;
    private final int durationInMinutes = 1;

    private final Encoder encoder;
    private final JsonWebToken jsonWebToken;
    private final EmailService emailService;

    @PersistenceContext
    private EntityManager entityManager;

    public AuthenticationService(AuthenticaltionUserRepository authenticatedUserRepository, Encoder encoder, JsonWebToken jsonWebToken, EmailService emailService) {
        this.authenticatedUserRepository = authenticatedUserRepository;
        this.encoder = encoder;
        this.jsonWebToken = jsonWebToken;
        this.emailService = emailService;
    }

    public static String generateEmailVerificationToken() {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            token.append(random.nextInt(10)); // Appending random digit from 0 to 9
        }

        return token.toString();
    }

    public void sendEmailVerificationToken(String email) {
        Optional<AuthenticationUser> user = authenticatedUserRepository.findByEmail(email);
        if (user.isPresent() && !user.get().getEmailVerified()) {
            String token = generateEmailVerificationToken();
            String hashedToken = encoder.encode(token);
            user.get().setEmailVerificationToken(hashedToken);
            user.get().setEmailVerificationTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMinutes));
            authenticatedUserRepository.save(user.get());
            String subject = "Email verification";

//            String body = String.format("Only one step to take full advantage of LinkedIn. \n\n"
//                    + "Enter this code to verify your email: " + "%s\n\n" + "The code will expire in "
//                    + durationInMinutes + " minutes.\n\n"
//            );
            String body = String.format("Only one step to take full advantage of LinkedIn. \n\n"
                            + "Enter this code to verify your email: " + "%s\n\n" + "The code will expire in " +
                    "%s" + " minutes.", token, durationInMinutes
            );

            try{
                emailService.sendEmail(email, subject, body);
            } catch (Exception e){
                logger.info("Error while sending email; {}", e.getMessage());
            }
        } else {
            throw new RuntimeException("Email verification failed or email already verified");
        }
    }

    public void validateEmailVerificationToken(String token, String email) {
        Optional<AuthenticationUser> user = authenticatedUserRepository.findByEmail(email);
        if (user.isPresent() && encoder.matches(token, user.get().getEmailVerificationToken())
                && !user.get().getEmailVerificationTokenExpiryDate().isBefore(LocalDateTime.now())) {
            user.get().setEmailVerified(true);
            user.get().setEmailVerificationToken(null);
            user.get().setEmailVerificationTokenExpiryDate(null);
            authenticatedUserRepository.save(user.get());
        } else if ( user.isPresent() && encoder.matches(token, user.get().getEmailVerificationToken())
                && user.get().getEmailVerificationTokenExpiryDate().isBefore(LocalDateTime.now())
        ) {
            throw new IllegalArgumentException("Email verification token expired");
        } else {
            throw new IllegalArgumentException("Email verification token expired");
        }
    }

    public AuthenticationUser getUser(String email) {
        return authenticatedUserRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public AuthenticationResponseBody register(AuthenticationRequestBody registerRequestBody) {
        AuthenticationUser user =  authenticatedUserRepository.save(new AuthenticationUser(registerRequestBody.getEmail(), encoder.encode(registerRequestBody.getPassword())));

         String emailVerificationToken = generateEmailVerificationToken();
         String hashedToken = encoder.encode(emailVerificationToken);
         user.setEmailVerificationToken(hashedToken);
         user.setEmailVerificationTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMinutes));

         authenticatedUserRepository.save(user);
         String subject = "Email verification";
         String body = String.format("""
                 Only one step to take full advantage of LinkedIn.
                 
                 Enter this code to verify your email: %s. The code will expire in %s
                 """, emailVerificationToken, durationInMinutes); // Include the token in the message body

        try{
            emailService.sendEmail(user.getEmail(), subject, body);
        } catch (Exception e) {
            logger.info("Error while sending email; {}", e.getMessage());
        }
        String authToken = jsonWebToken.generateToken(user.getEmail());
        return new AuthenticationResponseBody(authToken, "User registered success");
    }

    public AuthenticationResponseBody login(AuthenticationRequestBody loginRequestBody) {
        AuthenticationUser user = authenticatedUserRepository.findByEmail(loginRequestBody
                .getEmail()).orElseThrow(()-> new IllegalArgumentException("User not found"));

        if (!encoder.matches(loginRequestBody.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Password is not correct");
        }

        String token = jsonWebToken.generateToken(loginRequestBody.getEmail());
        return new AuthenticationResponseBody(token, "Authentication successful!");

    }

    public void sendPasswordResetToken(String email) {
        Optional<AuthenticationUser> user = authenticatedUserRepository.findByEmail(email);
        if (user.isPresent()) {
            String passwordResetToken = generateEmailVerificationToken();
            String hashedToken = encoder.encode(passwordResetToken);
            user.get().setPasswordResetToken(hashedToken);
            user.get().setPasswordResetTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMinutes));
            authenticatedUserRepository.save(user.get());
            String subject = "Password Reset";
            String body = String.format("""
                            You requested a password reset.
                            
                            Enter this code to reset your password: %s. The code will expire in %s minutes.""",
                    passwordResetToken, durationInMinutes);
            try {
                emailService.sendEmail(email, subject, body);
            } catch (Exception e) {
                logger.info("Error while sending email: {}", e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    public void resetPassword(String email, String newPassword, String token) {
        Optional<AuthenticationUser> user = authenticatedUserRepository.findByEmail(email);
        if (user.isPresent() && encoder.matches(token, user.get().getPasswordResetToken()) && !user.get().getPasswordResetTokenExpiryDate().isBefore(LocalDateTime.now())) {
            user.get().setPasswordResetToken(null);
            user.get().setPasswordResetTokenExpiryDate(null);
            user.get().setPassword(encoder.encode(newPassword));
            authenticatedUserRepository.save(user.get());
        } else if (user.isPresent() && encoder.matches(token, user.get().getPasswordResetToken()) && user.get().getPasswordResetTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Password reset token expired.");
        } else {
            throw new IllegalArgumentException("Password reset token failed.");
        }
    }

    public AuthenticationUser updateUserProfile(UUID userId, String firstName, String lastName, String company, String position, String location) {
        AuthenticationUser user = authenticatedUserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if(firstName != null) user.setFirstName(firstName);
        if(lastName != null) user.setLastName(lastName);
        if (company != null) user.setCompany(company);
        if (position != null) user.setPosition(position);
        if (location != null) user.setLocation(location);

        return authenticatedUserRepository.save(user);
    }

    @Transactional // rollback if could not delete all i.e it is all or nothing
    public void deleteUser(UUID userId) {
        AuthenticationUser user = entityManager.find(AuthenticationUser.class, userId);
        if (user != null) {
            entityManager.createNativeQuery("DELETE FROM posts_likes WHERE user_id = :userId" )
                    .setParameter("userId", userId)
                    .executeUpdate();
        }
        authenticatedUserRepository.deleteById(userId);
    }

    public List<AuthenticationUser> getUsersWithoutAuthenticated(AuthenticationUser user) {
        return authenticatedUserRepository.findAllByIdNot(user.getId());
    }

    public AuthenticationUser getUserById(UUID receiverId) {
        return authenticatedUserRepository.findById(receiverId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
