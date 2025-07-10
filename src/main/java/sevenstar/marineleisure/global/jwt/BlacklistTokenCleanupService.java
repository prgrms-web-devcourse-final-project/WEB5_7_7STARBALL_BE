package sevenstar.marineleisure.global.jwt;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlacklistTokenCleanupService {
	private final BlacklistedRefreshTokenRepository repository;

	@Scheduled(cron = "0 0 0 * * ?")
	@Transactional
	public void cleanupExpiredTokens() {
		LocalDateTime now = LocalDateTime.now();
		log.info("Starting cleanup of expired blacklisted refresh tokens at {}", now);
		try {
			repository.deleteExpiredTokens(now);
			log.info("Finished cleanup of expired blacklisted refresh tokens at {}", now);
		} catch (Exception e) {
			log.error("Error while cleaning up expired blacklisted refresh tokens at {}", now, e);
			throw new RuntimeException(
				"Error while cleaning up expired blacklisted refresh tokens at " + now + ": " + e.getMessage());
		} finally {
			log.info("Cleanup of expired blacklisted refresh tokens at {} finished", now);
		}
	}

}

