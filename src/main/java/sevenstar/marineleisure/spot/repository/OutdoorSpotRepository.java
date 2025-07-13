package sevenstar.marineleisure.spot.repository;

import java.util.Optional;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import sevenstar.marineleisure.spot.domain.OutdoorSpot;

public interface OutdoorSpotRepository extends JpaRepository<OutdoorSpot, Long> {
	Optional<OutdoorSpot> findByLocation(String location);

	@Query(value =
			"SELECT *, ST_Distance_Sphere(POINT(longitude, latitude), POINT(:longitude, :latitude)) as distance_in_meters " +
			"FROM outdoor_spot " +
			"ORDER BY distance_in_meters ASC " +
			"LIMIT :limit"
		, nativeQuery = true)
	List<OutdoorSpot> findByCoordinates(BigDecimal latitude, BigDecimal longitude, int limit);
}
