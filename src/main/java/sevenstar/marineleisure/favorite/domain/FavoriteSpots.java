package sevenstar.marineleisure.favorite.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sevenstar.marineleisure.global.domain.BaseEntity;

@Entity
@Table(name = "favorite_spot")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteSpots extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "spot_id", nullable = false)
	private Long spotId;

	@Column(nullable = false)
	private Boolean notification = true;

	@Builder
	public FavoriteSpots(Long userId, Long spotId) {
		this.userId = userId;
		this.spotId = spotId;
	}

}