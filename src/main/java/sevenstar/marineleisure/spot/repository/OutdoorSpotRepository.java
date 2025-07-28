package sevenstar.marineleisure.spot.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.dto.projection.SpotDistanceProjection;
import sevenstar.marineleisure.spot.dto.projection.BestSpotProjection;

public interface OutdoorSpotRepository extends JpaRepository<OutdoorSpot, Long> {

	Optional<OutdoorSpot> findByLatitudeAndLongitudeAndCategory(BigDecimal latitude, BigDecimal longitude,
		ActivityCategory category);

	@Query(value = """
		SELECT o.id, o.name, o.category, o.latitude, o.longitude,
		       ST_Distance_Sphere(o.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) AS distance
		FROM outdoor_spots o
		WHERE ST_Distance_Sphere(o.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) <= :radius
		  AND (:category IS NULL OR o.category = :category)
		""", nativeQuery = true)
	List<SpotDistanceProjection> findSpots(@Param("latitude") Float latitude, @Param("longitude") Float longitude,
		@Param("radius") double radius, @Param("category") String category);

	// Fishing Forecast
	@Query(value = """
		SELECT os.id AS id, os.name AS name, f.total_index AS totalIndex,
		         		COALESCE((
		                         	SELECT SUM(svs.view_count)
		                         	FROM spot_view_stats svs
		                         	WHERE svs.spot_id = os.id
		                         	  AND svs.view_date BETWEEN :forecastDate - INTERVAL 6 DAY AND :forecastDate
		                         ), 0) AS weekView,
		
		                         COALESCE((
		                         	SELECT SUM(svs.view_count)
		                         	FROM spot_view_stats svs
		                         	WHERE svs.spot_id = os.id
		                         	  AND svs.view_date BETWEEN :forecastDate - INTERVAL 29 DAY AND :forecastDate
		                         ), 0) AS monthView
		FROM outdoor_spots os
		JOIN fishing_forecast f ON os.id = f.spot_id
		WHERE f.forecast_date = :forecastDate
		  AND ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) <= :radius
		ORDER BY
		  CASE f.total_index
		    WHEN 'IMPOSSIBLE' THEN -1
		    WHEN 'VERY_BAD' THEN (1.0/5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326))/1000 + 1)) / 2
		    WHEN 'BAD' THEN (2.0/5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326))/1000 + 1)) / 2
		    WHEN 'NORMAL' THEN (3.0/5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326))/1000 + 1)) / 2
		    WHEN 'GOOD' THEN (4.0/5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326))/1000 + 1)) / 2
		    WHEN 'VERY_GOOD' THEN (5.0/5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326))/1000 + 1)) / 2
		  END DESC
		LIMIT 1
		""", nativeQuery = true)
	Optional<BestSpotProjection> findBestSpotInFishing(@Param("latitude") double latitude,
		@Param("longitude") double longitude, @Param("forecastDate") LocalDate forecastDate,
		@Param("radius") double radius);

	// Mudflat Forecast
	@Query(value = """
		SELECT os.id AS id, os.name AS name, m.total_index AS totalIndex,
		         		COALESCE((
		                         	SELECT SUM(svs.view_count)
		                         	FROM spot_view_stats svs
		                         	WHERE svs.spot_id = os.id
		                         	  AND svs.view_date BETWEEN :forecastDate - INTERVAL 6 DAY AND :forecastDate
		                         ), 0) AS weekView,
		
		                         COALESCE((
		                         	SELECT SUM(svs.view_count)
		                         	FROM spot_view_stats svs
		                         	WHERE svs.spot_id = os.id
		                         	  AND svs.view_date BETWEEN :forecastDate - INTERVAL 29 DAY AND :forecastDate
		                         ), 0) AS monthView
		FROM outdoor_spots os
		JOIN mudflat_forecast m ON os.id = m.spot_id
		WHERE m.forecast_date = :forecastDate
		  AND ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) <= :radius
		ORDER BY
		  CASE m.total_index
		    WHEN 'IMPOSSIBLE' THEN -1
		    WHEN 'VERY_BAD' THEN (1.0/5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326))/1000 + 1)) / 2
		    WHEN 'BAD' THEN (2.0/5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326))/1000 + 1)) / 2
		    WHEN 'NORMAL' THEN (3.0/5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326))/1000 + 1)) / 2
		    WHEN 'GOOD' THEN (4.0/5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326))/1000 + 1)) / 2
		    WHEN 'VERY_GOOD' THEN (5.0/5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326))/1000 + 1)) / 2
		  END DESC
		LIMIT 1
		""", nativeQuery = true)
	Optional<BestSpotProjection> findBestSpotInMudflat(@Param("latitude") double latitude,
		@Param("longitude") double longitude, @Param("forecastDate") LocalDate forecastDate,
		@Param("radius") double radius);

	// Surfing Forecast
	@Query(value = """
		SELECT os.id AS id, os.name AS name, s.total_index AS totalIndex,
		         		COALESCE((
		                         	SELECT SUM(svs.view_count)
		                         	FROM spot_view_stats svs
		                         	WHERE svs.spot_id = os.id
		                         	  AND svs.view_date BETWEEN :forecastDate - INTERVAL 6 DAY AND :forecastDate
		                         ), 0) AS weekView,
		
		                         COALESCE((
		                         	SELECT SUM(svs.view_count)
		                         	FROM spot_view_stats svs
		                         	WHERE svs.spot_id = os.id
		                         	  AND svs.view_date BETWEEN :forecastDate - INTERVAL 29 DAY AND :forecastDate
		                         ), 0) AS monthView
		FROM outdoor_spots os
		JOIN surfing_forecast s ON os.id = s.spot_id
		WHERE s.forecast_date = :forecastDate
		  AND ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) <= :radius
		ORDER BY
		  CASE s.total_index
		    WHEN 'IMPOSSIBLE' THEN -1
		    WHEN 'VERY_BAD' THEN (1.0/5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326))/1000 + 1)) / 2
		    WHEN 'BAD' THEN (2.0/5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326))/1000 + 1)) / 2
		    WHEN 'NORMAL' THEN (3.0/5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326))/1000 + 1)) / 2
		    WHEN 'GOOD' THEN (4.0/5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326))/1000 + 1)) / 2
		    WHEN 'VERY_GOOD' THEN (5.0/5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326))/1000 + 1)) / 2
		  END DESC
		LIMIT 1
		""", nativeQuery = true)
	Optional<BestSpotProjection> findBestSpotInSurfing(@Param("latitude") double latitude,
		@Param("longitude") double longitude, @Param("forecastDate") LocalDate forecastDate,
		@Param("radius") double radius);

	// Scuba Forecast
	@Query(value = """
		SELECT os.id AS id, os.name AS name, s.total_index AS totalIndex,
		         		COALESCE((
		                         	SELECT SUM(svs.view_count)
		                         	FROM spot_view_stats svs
		                         	WHERE svs.spot_id = os.id
		                         	  AND svs.view_date BETWEEN :forecastDate - INTERVAL 6 DAY AND :forecastDate
		                         ), 0) AS weekView,
		
		                         COALESCE((
		                         	SELECT SUM(svs.view_count)
		                         	FROM spot_view_stats svs
		                         	WHERE svs.spot_id = os.id
		                         	  AND svs.view_date BETWEEN :forecastDate - INTERVAL 29 DAY AND :forecastDate
		                         ), 0) AS monthView
		FROM outdoor_spots os
		JOIN scuba_forecast s ON os.id = s.spot_id
		WHERE s.forecast_date = :forecastDate
		  AND ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) <= :radius
		ORDER BY
		  CASE s.total_index
		    WHEN 'IMPOSSIBLE' THEN -1
		    WHEN 'VERY_BAD' THEN (1.0/5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326))/1000 + 1)) / 2
		    WHEN 'BAD' THEN (2.0/5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326))/1000 + 1)) / 2
		    WHEN 'NORMAL' THEN (3.0/5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326))/1000 + 1)) / 2
		    WHEN 'GOOD' THEN (4.0/5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326))/1000 + 1)) / 2
		    WHEN 'VERY_GOOD' THEN (5.0/5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326))/1000 + 1)) / 2
		  END DESC
		LIMIT 1
		""", nativeQuery = true)
	Optional<BestSpotProjection> findBestSpotInScuba(@Param("latitude") double latitude,
		@Param("longitude") double longitude, @Param("forecastDate") LocalDate forecastDate,
		@Param("radius") double radius);

	@Query(value = """
		SELECT *, ST_Distance_Sphere(POINT(longitude, latitude), POINT(:longitude, :latitude)) AS distance_in_meters
		FROM outdoor_spots
		ORDER BY distance_in_meters ASC
		LIMIT :limit
		""", nativeQuery = true)
	List<OutdoorSpot> findByCoordinates(@Param("latitude") BigDecimal latitude,
		@Param("longitude") BigDecimal longitude, @Param("limit") int limit);
}
