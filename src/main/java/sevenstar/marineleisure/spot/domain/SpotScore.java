package sevenstar.marineleisure.spot.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 이후 각 시/도에 기반한 프리셋 구현에 사용될 엔티티입니다
 * @author gunwoong
 */
// TODO : 기능 고도화에 사용될 프리셋
@Entity
@Table(name = "spot_score")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SpotScore {
	@Id
	private Long spotId;
	private Double score;
}
