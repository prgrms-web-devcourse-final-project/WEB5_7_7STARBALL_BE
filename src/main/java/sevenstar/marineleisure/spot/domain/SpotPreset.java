package sevenstar.marineleisure.spot.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
@Table(name = "spot_preset")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SpotPreset {
	@Id
	@Enumerated(EnumType.STRING)
	private Region region;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "spotId",column = @Column(name = "fishing_spot_id")),
		@AttributeOverride(name = "name",column = @Column(name = "fishing_name")),
		@AttributeOverride(name = "totalIndex",column = @Column(name = "fishing_total_index"))
	})
	private BestSpot fishing;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "spotId",column = @Column(name = "mudflat_spot_id")),
		@AttributeOverride(name = "name",column = @Column(name = "mudflat_name")),
		@AttributeOverride(name = "totalIndex",column = @Column(name = "mudflat_total_index"))
	})
	private BestSpot mudflat;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "spotId",column = @Column(name = "scuba_spot_id")),
		@AttributeOverride(name = "name",column = @Column(name = "scuba_name")),
		@AttributeOverride(name = "totalIndex",column = @Column(name = "scuba_total_index"))
	})
	private BestSpot scuba;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "spotId",column = @Column(name = "surfing_spot_id")),
		@AttributeOverride(name = "name",column = @Column(name = "surfing_name")),
		@AttributeOverride(name = "totalIndex",column = @Column(name = "surfing_total_index"))
	})
	private BestSpot surfing;

}
