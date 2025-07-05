package sevenstar.marineleisure.member.domain;

import java.math.BigDecimal;

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

	@Column(nullable = false, length = 10)
	private String nickname;

	@Column(nullable = false, length = 50)
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

}