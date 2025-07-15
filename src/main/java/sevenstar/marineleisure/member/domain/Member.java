package sevenstar.marineleisure.member.domain;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sevenstar.marineleisure.global.domain.BaseEntity;
import sevenstar.marineleisure.global.enums.MemberStatus;

@Entity
@Getter
@Table(name = "members")
@NoArgsConstructor
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 20, unique = true)
	private String nickname;

	@Column(nullable = false, length = 50, unique = true)
	private String email;

	private String provider;
	@Column(name = "provider_id")
	private String providerId;

	@Column(nullable = false)
	private MemberStatus status = MemberStatus.ACTIVE;

	@Column(precision = 9, scale = 6)
	private BigDecimal latitude;

	@Column(precision = 9, scale = 6)
	private BigDecimal longitude;

	@Builder
	public Member(String nickname, String email, String provider, String providerId,
		BigDecimal latitude, BigDecimal longitude) {
		this.nickname = nickname;
		this.email = email;
		this.provider = provider;
		this.providerId = providerId;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public void updateNickname(String newNickname) {
		if (!Objects.equals(this.nickname, newNickname)) {
			this.nickname = newNickname;
		}
	}

	/**
	 * 회원의 상태를 업데이트합니다.
	 *
	 * @param newStatus 새 상태
	 */
	public void updateStatus(MemberStatus newStatus) {
		if (this.status != newStatus) {
			this.status = newStatus;
		}
	}

	/**
	 * 회원의 위치 정보를 업데이트합니다.
	 *
	 * @param newLatitude 새 위도
	 * @param newLongitude 새 경도
	 */
	public void updateLocation(BigDecimal newLatitude, BigDecimal newLongitude) {
		if (newLatitude != null && (this.latitude == null || !this.latitude.equals(newLatitude))) {
			this.latitude = newLatitude;
		}
		if (newLongitude != null && (this.longitude == null || !this.longitude.equals(newLongitude))) {
			this.longitude = newLongitude;
		}
	}
}