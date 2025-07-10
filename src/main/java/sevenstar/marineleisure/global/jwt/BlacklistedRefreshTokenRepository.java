package sevenstar.marineleisure.global.jwt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BlacklistedRefreshTokenRepository extends JpaRepository<BlacklistedRefreshToken, Long> {
	Optional<BlacklistedRefreshToken> findByJti(String jti);

	boolean existsByJti(String jti);

	@Modifying
	@Query("DELETE FROM BlacklistedRefreshToken b WHERE b.expiryDate < :now")
	void deleteExpiredTokens(@Param("now") LocalDateTime now);
}
