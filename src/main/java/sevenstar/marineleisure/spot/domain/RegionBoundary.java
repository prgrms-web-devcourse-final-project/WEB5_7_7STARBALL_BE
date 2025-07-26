package sevenstar.marineleisure.spot.domain;

import org.locationtech.jts.geom.MultiPolygon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sevenstar.marineleisure.global.enums.Region;

@Entity
@Table(name = "region_boundary")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RegionBoundary {
	@Id
	@Enumerated(EnumType.STRING)
	private Region region;

	@Column(columnDefinition = "MULTIPOLYGON SRID 4326")
	private MultiPolygon geom;

	public RegionBoundary(Region region, MultiPolygon geom) {
		this.region = region;
		this.geom = geom;
	}
}
