package com.example.hena.security;

import com.example.hena.user.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;


@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private static final long EXPIRATION_TIME_MS = 3600000 ;

    @Value("${jwt.secret:VGhpcyBpcyBhIHN0cm9uZyBzZWNyZXQga2V5IGZvcjBpbmdhdG9ycyE=}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms:3600000}")
    private long jwtExpirationMs;

    private SecretKey key() {
        if (jwtSecret != null && !jwtSecret.isEmpty()) {
            byte[] decodedKey = Decoders.BASE64URL.decode(jwtSecret);
            if (decodedKey.length >= 32) {
                return Keys.hmacShaKeyFor(decodedKey);
            } else {
                throw new RuntimeException("JWT secret key must be at least 256 bits long.");
            }
        } else {
            return Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        }
    }

    public String generateJwtToken(Authentication authentication) throws Exception {
        User userPrincipal = (User) authentication.getPrincipal();
        String header = base64UrlEncode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        long now = System.currentTimeMillis();
        long exp = now + jwtExpirationMs;
        String payload = String.format("{\"sub\":\"%s\",\"iat\":%d,\"exp\":%d}",
                userPrincipal.getUsername(), now / 1000, exp / 1000);
        String encodedPayload = base64UrlEncode(payload);

        String signatureInput = header + "." + encodedPayload;
        String signature = base64UrlEncode(hmacSha256(signatureInput, key().getEncoded()));

        return signatureInput + "." + signature;
    }

    public String getUserNameFromJwtToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token format.");
            }
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            logger.debug("Decoded JWT payload: {}", payload);
            return payload.split("\"sub\":\"")[1].split("\"")[0];
        } catch (Exception e) {
            logger.error("Failed to extract username from JWT token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token.");
        }
    }

    public boolean validateJwtToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token format.");
            }
            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];

            String signatureInput = header + "." + payload;
            byte[] expectedSignature = hmacSha256(signatureInput, key().getEncoded());

            byte[] providedSignature = Base64.getUrlDecoder().decode(signature);

            // Timing-safe comparison of byte arrays
            if (!constantTimeArrayEquals(expectedSignature, providedSignature)) {
                logger.error("JWT signature validation failed.");
                return false;
            }

            // Optional: validate expiration
            String decodedPayload = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
            long exp = Long.parseLong(decodedPayload.split("\"exp\":")[1].split("}")[0]);
            long nowSeconds = System.currentTimeMillis() / 1000;
            if (nowSeconds > exp) {
                logger.error("JWT token expired.");
                return false;
            }

            return true;
        } catch (Exception e) {
            logger.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    private byte[] hmacSha256(String data, byte[] key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(key, "HmacSHA256");
        mac.init(keySpec);
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    private boolean constantTimeArrayEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }

    private String base64UrlEncode(String str) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String getEmailFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token format.");
            }
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            // crude manual parsing for "sub" field:
            return payloadJson.split("\"sub\":\"")[1].split("\"")[0];
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token.", e);
        }
    }

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // Static method to generate JWT token from email
    public static String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(key)
                .compact();
    }

}
