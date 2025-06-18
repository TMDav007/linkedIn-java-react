package com.linkedIn.backend.features.authentication.controller;

import com.linkedIn.backend.dto.Response;
import com.linkedIn.backend.features.authentication.dto.AuthenticationOauthRequestBody;
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

    @PostMapping("/login")
    public AuthenticationResponseBody loginPage(@Valid @RequestBody AuthenticationRequestBody loginRequestBody) {
        return authenticationService.login(loginRequestBody);
    }

    @PostMapping("/oauth/google/login")
    public AuthenticationResponseBody googleLogin(@RequestBody AuthenticationOauthRequestBody oauth2RequestBody) {
        return authenticationService.googleLoginOrSignup(oauth2RequestBody.code(), oauth2RequestBody.page());
    }

    @PostMapping("/register")
    public AuthenticationResponseBody registerPage(@Valid @RequestBody AuthenticationRequestBody registerRequestBody) {
            return authenticationService.register(registerRequestBody);
    }

    @PutMapping("/validate-email-verification-token")
    public Response verifyEmail(@RequestParam("token") String token, @RequestAttribute("authenticatedUser") AuthenticationUser user) {
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
    public Response resetPassword(@RequestParam("newPassword") String newPassword, @RequestParam String token, @RequestParam String email) {
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
            @PathVariable("id") UUID id,
            @RequestParam(name="firstName",required = false) String firstName,
            @RequestParam(name="lastName",required = false) String lastName,
            @RequestParam(name="company",required = false) String company,
            @RequestParam(name="position",required = false) String position,
            @RequestParam(name="location",required = false) String location,
            @RequestParam(name="profilePicture",required = false) String profilePicture,
            @RequestParam(name="coverPicture",required = false) String coverPicture,
            @RequestParam(name="about",required = false) String about

    ) {
        if (!user.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have permission to update this profile.");
        }

        return authenticationService.updateUserProfile(id, firstName, lastName, company, position, location, profilePicture, coverPicture, about);
    }

    @GetMapping("/users")
    public List<AuthenticationUser> getUsersWithoutAuthenticated (@RequestAttribute("authenticatedUser") AuthenticationUser user) {
       return authenticationService.getUsersWithoutAuthenticated(user);
    }


    @GetMapping("/users/me")
    public AuthenticationUser getUser(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        return user;
    }

    @GetMapping("/users/{id}")
    public AuthenticationUser getUserById(@PathVariable("id")  UUID id) {
        return authenticationService.getUserById(id);
    }

}
