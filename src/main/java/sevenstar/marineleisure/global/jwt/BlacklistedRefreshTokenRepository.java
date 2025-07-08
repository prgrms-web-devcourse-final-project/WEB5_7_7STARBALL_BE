package sevenstar.marineleisure.global.jwt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlacklistedRefreshTokenRepository extends JpaRepository<BlacklistedRefreshToken, Long> {
    Optional<BlacklistedRefreshToken> findByJti(String jti);
    boolean existsByJti(String jti);
}
