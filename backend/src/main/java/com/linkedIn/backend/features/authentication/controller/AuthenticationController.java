package com.linkedIn.backend.features.authentication.controller;

import com.linkedIn.backend.dto.Response;
import com.linkedIn.backend.features.authentication.dto.AuthenticationRequestBody;
import com.linkedIn.backend.features.authentication.dto.AuthenticationResponseBody;
import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import com.linkedIn.backend.features.authentication.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/user")
    public AuthenticationUser getUser(@RequestAttribute("authenticatedUser") AuthenticationUser authenticatedUser) {
        return authenticationService.getUser(authenticatedUser.getEmail());
    }

    @PostMapping("/login")
    public AuthenticationResponseBody loginPage(@Valid @RequestBody AuthenticationRequestBody loginRequestBody) {
        return authenticationService.login(loginRequestBody);
    }


    @PostMapping("/register")
    public AuthenticationResponseBody registerPage(@Valid @RequestBody AuthenticationRequestBody registerRequestBody) {
            return authenticationService.register(registerRequestBody);
    }

    @PutMapping("/validate-email-verification-token")
    public Response verifyEmail(@RequestParam String token, @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        authenticationService.validateEmailVerificationToken(token, user.getEmail());
        return new Response("Email verified successfully.");
    }

    @GetMapping("/send-email-verification-token")
    public Response sendEmailVerificationToken(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        authenticationService.sendEmailVerificationToken(user.getEmail());
        return new Response("Email verification token sent successfully.");
    }

    @PutMapping("/send-password-reset-token")
    public Response sendPasswordResetToken(@RequestParam String email) {
        authenticationService.sendPasswordResetToken(email);
        return new Response("Password reset token sent successfully.");
    }

    @PutMapping("/reset-password")
    public Response resetPassword(@RequestParam String newPassword, @RequestParam String token, @RequestParam String email) {
        authenticationService.resetPassword(email, newPassword, token);
        return new Response("Password reset successfully.");
    }

    @DeleteMapping("/delete")
    public Response deleteUser(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        authenticationService.deleteUser(user.getId());
        return new Response("User deleted successfully.");
    }

    @PutMapping("/profile/{id}")
    public AuthenticationUser updateUserProfile(
            @RequestAttribute("authenticatedUser") AuthenticationUser user,
            @PathVariable UUID id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String location
    ) {
        if (!user.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have permission to update this profile.");
        }

        return authenticationService.updateUserProfile(id, firstName, lastName, company, position, location);
    }

    @GetMapping("/users")
    public List<AuthenticationUser> getUsersWithoutAuthenticated (@RequestAttribute("authenticatedUser") AuthenticationUser user) {
       return authenticationService.getUsersWithoutAuthenticated(user);
    }

}
