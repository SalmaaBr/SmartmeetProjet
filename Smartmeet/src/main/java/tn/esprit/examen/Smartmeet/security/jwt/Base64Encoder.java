package tn.esprit.examen.Smartmeet.security.jwt;

import java.security.SecureRandom;
import java.util.Base64;

public class Base64Encoder {
    public static void main(String[] args) {


        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[32];
        secureRandom.nextBytes(keyBytes);
        String secretKey = Base64.getEncoder().encodeToString(keyBytes);
        System.out.println("Generated SECRET_KEY: " + secretKey);
    }
}

