package sevenstar.marineleisure.alert.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import sevenstar.marineleisure.alert.domain.JellyfishRegionDensity;
import sevenstar.marineleisure.alert.dto.vo.JellyfishDetailVO;

public interface JellyfishRegionDensityRepository extends JpaRepository<JellyfishRegionDensity, Long> {

	@Query(value = """
		SELECT 
		    s.name AS species,
		    r.region_name AS region,
		    r.density_type AS densityType,
		    s.toxicity AS toxicity,
		    r.report_date AS reportDate
		FROM jellyfish_region_density r
		JOIN jellyfish_species s ON r.species = s.id
		WHERE r.report_date = (
		    SELECT MAX(r2.report_date) FROM jellyfish_region_density r2
		)
		""", nativeQuery = true)
	List<JellyfishDetailVO> findLatestJellyfishDetails();
}
