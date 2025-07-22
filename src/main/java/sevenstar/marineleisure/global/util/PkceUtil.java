package sevenstar.marineleisure.global.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class PkceUtil {
	public String generateCodeVerifier() {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[64];
		random.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	public String generateCodeChallenge(String codeVerifier) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] bytes = digest.digest(codeVerifier.getBytes());
			return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
		} catch (Exception e) {
			throw new RuntimeException("Failed to generate code challenge", e);
		}
	}

	public boolean verifyCodeChallenge(String codeChallenge, String codeVerifier) {
		return false;
	}
}
