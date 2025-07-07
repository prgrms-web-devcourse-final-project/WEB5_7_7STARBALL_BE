package sevenstar.marineleisure.spot.domain;

import java.math.BigDecimal;

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
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.FishingType;

@Entity
@Getter
@Table(name = "outdoor_spots")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutdoorSpot extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private ActivityCategory category;

	private FishingType type;

	@Column(length = 100)
	private String location;

	@Column(precision = 9, scale = 6)
	private BigDecimal latitude;

	@Column(precision = 9, scale = 6)
	private BigDecimal longitude;

	@Builder
	public OutdoorSpot(String name, ActivityCategory category, FishingType type, String location, BigDecimal latitude,
		BigDecimal longitude) {
		this.name = name;
		this.category = category;
		this.type = type;
		this.location = location;
		this.latitude = latitude;
		this.longitude = longitude;
	}
}
