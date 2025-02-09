package com.store.util;

import java.security.SecureRandom;
import java.util.Base64;

public class GenerateSecretKey {
    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[32]; // 256-bit key
        random.nextBytes(key);
        String base64Key = Base64.getEncoder().encodeToString(key);
        System.out.println("Generated Secret Key: " + base64Key);
    }
}