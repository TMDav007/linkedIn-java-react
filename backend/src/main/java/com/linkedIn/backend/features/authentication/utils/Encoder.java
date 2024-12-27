package com.linkedIn.backend.features.authentication.utils;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class Encoder {

    public String encode(String rawString) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawString.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error encoding string",e);
        }
    }

    public boolean matches(String rawString, String encodedString) {
        return encode(rawString).equals(encodedString);
    }
}
