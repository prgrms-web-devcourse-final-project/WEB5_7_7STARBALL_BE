package sevenstar.marineleisure.spot.domain;

import java.math.BigDecimal;

import org.locationtech.jts.geom.Point;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(name = "outdoor_spots", indexes = {
	@Index(name = "idx_lat_lon", columnList = "latitude, longitude"),
	@Index(name = "idx_point", columnList = "geo_point")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutdoorSpot extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	private ActivityCategory category;

	@Enumerated(EnumType.STRING)
	private FishingType type;

	@Column(length = 100)
	private String location;

	@Column(precision = 9, scale = 6)
	private BigDecimal latitude;

	@Column(precision = 9, scale = 6)
	private BigDecimal longitude;

	@Column(name = "geo_point", columnDefinition = "POINT SRID 4326",
		nullable = false)
	private Point point;

	@Builder
	public OutdoorSpot(String name, ActivityCategory category, FishingType type, String location, BigDecimal latitude,
		BigDecimal longitude, Point point) {
		this.name = name;
		this.category = category;
		this.type = type;
		this.location = location;
		this.latitude = latitude;
		this.longitude = longitude;
		this.point = point;
	}
}
