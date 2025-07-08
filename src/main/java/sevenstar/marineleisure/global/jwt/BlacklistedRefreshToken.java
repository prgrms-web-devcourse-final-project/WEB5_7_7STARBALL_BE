package sevenstar.marineleisure.global.jwt;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sevenstar.marineleisure.global.domain.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "blacklisted_refresh_tokens")
@Getter
@NoArgsConstructor
public class BlacklistedRefreshToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String jti;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Builder
    public BlacklistedRefreshToken(String jti, Long memberId, LocalDateTime expiryDate) {
        this.jti = jti;
        this.memberId = memberId;
        this.expiryDate = expiryDate;
    }
}
