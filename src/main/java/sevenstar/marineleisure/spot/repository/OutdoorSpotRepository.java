package sevenstar.marineleisure.spot.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.dto.SpotDistanceProjection;

public interface OutdoorSpotRepository extends JpaRepository<OutdoorSpot, Long> {
	Optional<OutdoorSpot> findByLatitudeAndLongitudeAndCategory(BigDecimal latitude, BigDecimal longitude,
		ActivityCategory category);

	@Query(value = """
		SELECT o.id, o.name, o.category,o.latitude,o.longitude,ST_Distance_Sphere(o.geo_point, ST_SRID(POINT(:clientLon, :clientLat),4326)) as distance
		FROM outdoor_spots o
		""", nativeQuery = true)
	List<SpotDistanceProjection> findBySpotDistanceInstanceByLatitudeAndLongitude(
		@Param("clientLat") Float clientLat, @Param("clientLon") Float clientLon);

	@Query(value = """
		SELECT o.id, o.name, o.category,o.latitude,o.longitude,ST_Distance_Sphere(o.geo_point, ST_SRID(POINT(:clientLon, :clientLat),4326)) AS distance
		FROM outdoor_spots o
		WHERE ST_Distance_Sphere(o.geo_point, ST_SRID(POINT(:clientLon, :clientLat),4326)) <= :radius
		""", nativeQuery = true)
	List<SpotDistanceProjection> findBySpotDistanceInstanceByLatitudeAndLongitude(
		@Param("clientLat") Float clientLat, @Param("clientLon") Float clientLon, @Param("radius") double radius);

	@Query(value = """
		SELECT o.id, o.name, o.category,o.latitude,o.longitude,ST_Distance_Sphere(o.geo_point, ST_SRID(POINT(:clientLon, :clientLat),4326)) as distance
		FROM outdoor_spots o
				WHERE o.category = :category
		""", nativeQuery = true)
	List<SpotDistanceProjection> findBySpotDistanceInstanceByLatitudeAndLongitudeAndCategory(
		@Param("clientLat") Float clientLat, @Param("clientLon") Float clientLon, @Param("category") String category);

	@Query(value = """
		SELECT o.id, o.name, o.category,o.latitude,o.longitude,ST_Distance_Sphere(o.geo_point, ST_SRID(POINT(:clientLon, :clientLat),4326)) as distance
		FROM outdoor_spots o
				WHERE o.category = :category AND ST_Distance_Sphere(o.geo_point, ST_SRID(POINT(:clientLon, :clientLat),4326)) <= :radius
		""", nativeQuery = true)
	List<SpotDistanceProjection> findBySpotDistanceInstanceByLatitudeAndLongitudeAndCategory(
		@Param("clientLat") Float clientLat, @Param("clientLon") Float clientLon, @Param("category") String category,
		@Param("radius") double radius);
}
