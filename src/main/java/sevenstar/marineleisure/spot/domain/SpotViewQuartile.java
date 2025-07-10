package sevenstar.marineleisure.spot.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "spot_view_quartile")
public class SpotViewQuartile {
	@Id
	private Long spotId;
	@Column(name = "month_quartile")

	private Integer monthQuartile;
	@Column(name = "week_quartile")
	private Integer weekQuartile;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
}
