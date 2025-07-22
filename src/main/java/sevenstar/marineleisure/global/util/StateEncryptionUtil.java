package sevenstar.marineleisure.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * OAuth 상태 값 암호화/복호화 유틸리티
 * 세션 없이 상태 값을 안전하게 관리하기 위한 유틸리티.
 */
@Component
public class StateEncryptionUtil {

    @Value("${oauth.state.encryption.secret:defaultSecretKey}")
    private String secretKey;

    /**
     * 상태 값을 암호화합니다.
     *
     * @param state 암호화할 상태 값
     * @return 암호화된 상태 값 (Base64 인코딩)
     */
    public String encryptState(String state, String codeVerifier) {
        try {
            String combined = state + "|" + codeVerifier;

            SecretKeySpec keySpec = generateKey(secretKey);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(combined.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt state", e);
        }
    }


    public String extractCodeVerifier(String state) {
		try {
			String decrypted = decryptState(state);
			String[] parts = decrypted.split("\\|", 2);
			if (parts.length != 2) {
				throw new IllegalArgumentException("Invalid encrypted format");
			}
            return parts[1];
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Failed to extract code verifier", e);
		}
	}

    /**
     * 암호화된 상태 값을 복호화.
     *
     * @param encryptedState 암호화된 상태 값 (Base64 인코딩)
     * @return 복호화된 상태 값
     */
    public String decryptState(String encryptedState) {
        try {
            SecretKeySpec keySpec = generateKey(secretKey);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getUrlDecoder().decode(encryptedState);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt state", e);
        }
    }

    /**
     * 상태 값을 검증.
     *
     * @param state 원본 상태 값
     * @param encryptedState 암호화된 상태 값
     * @return 검증 결과 (true: 유효, false: 무효)
     */
    public boolean validateState(String state, String encryptedState) {
        try {
            String decryptedState = decryptState(encryptedState);
            String[] parts = decryptedState.split("\\|", 2);
            if (parts.length != 2) {
                return false;
            }
            return parts[0].equals(state);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 비밀 키를 생성.
     *
     * @param key 원본 비밀 키
     * @return AES 암호화에 사용할 키
     */
    private SecretKeySpec generateKey(String key) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        keyBytes = sha.digest(keyBytes);
        keyBytes = Arrays.copyOf(keyBytes, 16); // AES-128 키 길이
        return new SecretKeySpec(keyBytes, "AES");
    }
}
