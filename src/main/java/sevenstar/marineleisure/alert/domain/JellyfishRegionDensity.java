package sevenstar.marineleisure.alert.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sevenstar.marineleisure.global.domain.BaseEntity;
import sevenstar.marineleisure.global.enums.DensityLevel;

@Entity
@Table(name = "jellyfish_region_density")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JellyfishRegionDensity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "region_name", nullable = false, length = 100)
	private String regionName;

	@JoinColumn(name = "species_id", nullable = false)
	private Long species;

	@Column(name = "report_date", nullable = false)
	private LocalDate reportDate;

	@Column(name = "appearance_rate", length = 10)
	private String appearanceRate;

	@Column(name = "density_type", nullable = false, length = 10)
	private DensityLevel densityType;

	@Builder
	public JellyfishRegionDensity(
		String regionName,
		Long species,
		LocalDate reportDate,
		String appearanceRate,
		DensityLevel densityType
	) {
		this.regionName = regionName;
		this.species = species;
		this.reportDate = reportDate;
		this.appearanceRate = appearanceRate;
		this.densityType = densityType;
	}
}