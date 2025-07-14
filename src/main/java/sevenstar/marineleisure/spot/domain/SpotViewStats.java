package sevenstar.marineleisure.spot.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "spot_view_stats")
@IdClass(SpotViewStatsId.class)
public class SpotViewStats {

	@Id
	@Column(name = "spot_id", nullable = false)
	private Long spotId;

	@Id
	@Column(name = "view_date", nullable = false)
	private LocalDate viewDate;

	@Column(name = "view_count", nullable = false)
	private Integer viewCount;
}
