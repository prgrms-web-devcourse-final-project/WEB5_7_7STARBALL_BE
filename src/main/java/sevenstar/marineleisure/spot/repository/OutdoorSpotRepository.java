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
import sevenstar.marineleisure.spot.dto.projection.SpotPreviewProjection;

public interface OutdoorSpotRepository extends JpaRepository<OutdoorSpot, Long> {
	Optional<OutdoorSpot> findByLatitudeAndLongitudeAndCategory(BigDecimal latitude, BigDecimal longitude,
		ActivityCategory category);

	@Query(value = """
		SELECT o.id, o.name, o.category,o.latitude,o.longitude,ST_Distance_Sphere(o.geo_point, ST_SRID(POINT(:longitude, :latitude),4326)) AS distance
		FROM outdoor_spots o
		WHERE ST_Distance_Sphere(o.geo_point, ST_SRID(POINT(:longitude, :latitude),4326)) <= :radius
		""", nativeQuery = true)
	List<SpotDistanceProjection> findBySpotDistanceInstanceByLatitudeAndLongitude(@Param("latitude") Float latitude,
		@Param("longitude") Float longitude, @Param("radius") double radius);

	@Query(value = """
		SELECT o.id, o.name, o.category,o.latitude,o.longitude,ST_Distance_Sphere(o.geo_point, ST_SRID(POINT(:longitude, :latitude),4326)) as distance
		FROM outdoor_spots o
				WHERE o.category = :category AND ST_Distance_Sphere(o.geo_point, ST_SRID(POINT(:longitude, :latitude),4326)) <= :radius
		""", nativeQuery = true)
	List<SpotDistanceProjection> findBySpotDistanceInstanceByLatitudeAndLongitudeAndCategory(
		@Param("latitude") Float latitude, @Param("longitude") Float longitude, @Param("radius") double radius,
		@Param("category") String category);

	// TODO : 리팩토링 무조건 필요 (지점 기반 프리셋 생성후 프리뷰같은)
	@Query(value = """
		SELECT
		    os.id AS spotId,
		    os.name AS name,
		    f.total_index AS totalIndex
		FROM outdoor_spots os
		JOIN fishing_forecast f ON os.id = f.spot_id
		WHERE f.forecast_date = :forecastDate
		ORDER BY
		    CASE f.total_index
		        WHEN 'IMPOSSIBLE' THEN -1
		        WHEN 'VERY_BAD' THEN (1.0 / 5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) / 1000 + 1)) / 2
		        WHEN 'BAD' THEN (2.0 / 5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) / 1000 + 1)) / 2
		        WHEN 'NORMAL' THEN (3.0 / 5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) / 1000 + 1)) / 2
		        WHEN 'GOOD' THEN (4.0 / 5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) / 1000 + 1)) / 2
		        WHEN 'VERY_GOOD' THEN (5.0 / 5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) / 1000 + 1)) / 2
		    END DESC
		LIMIT 1
		""", nativeQuery = true)
	SpotPreviewProjection findBestSpotInFishing(@Param("latitude") double latitude,
		@Param("longitude") double longitude, @Param("forecastDate") LocalDate forecastDate);

	@Query(value = """
		    SELECT
		        os.id AS spotId,
		        os.name AS name,
		        m.total_index AS totalIndex
		    FROM outdoor_spots os
		    JOIN mudflat_forecast m ON os.id = m.spot_id
		    WHERE m.forecast_date = :forecastDate
		    ORDER BY
		        CASE m.total_index
					WHEN 'IMPOSSIBLE' THEN -1
					WHEN 'VERY_BAD' THEN (1.0 / 5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) / 1000 + 1)) / 2
					WHEN 'BAD' THEN (2.0 / 5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) / 1000 + 1)) / 2
					WHEN 'NORMAL' THEN (3.0 / 5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) / 1000 + 1)) / 2
					WHEN 'GOOD' THEN (4.0 / 5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) / 1000 + 1)) / 2
					WHEN 'VERY_GOOD' THEN (5.0 / 5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) / 1000 + 1)) / 2
		        END DESC
		    LIMIT 1
		""", nativeQuery = true)
	SpotPreviewProjection findBestSpotInMudflat(@Param("latitude") double latitude,
		@Param("longitude") double longitude, @Param("forecastDate") LocalDate forecastDate);

	@Query(value = """
		    SELECT
		        os.id AS spotId,
		        os.name AS name,
		        s.total_index AS totalIndex
		    FROM outdoor_spots os
		    JOIN surfing_forecast s ON os.id = s.spot_id
		    WHERE s.forecast_date = :forecastDate
		    ORDER BY
		        CASE s.total_index
					WHEN 'IMPOSSIBLE' THEN -1
					WHEN 'VERY_BAD' THEN (1.0 / 5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) / 1000 + 1)) / 2
					WHEN 'BAD' THEN (2.0 / 5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) / 1000 + 1)) / 2
					WHEN 'NORMAL' THEN (3.0 / 5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) / 1000 + 1)) / 2
					WHEN 'GOOD' THEN (4.0 / 5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) / 1000 + 1)) / 2
					WHEN 'VERY_GOOD' THEN (5.0 / 5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) / 1000 + 1)) / 2
		        END DESC
		    LIMIT 1
		""", nativeQuery = true)
	SpotPreviewProjection findBestSpotInSurfing(@Param("latitude") double latitude,
		@Param("longitude") double longitude, @Param("forecastDate") LocalDate forecastDate);

	@Query(value = """
		    SELECT
		        os.id AS spotId,
		        os.name AS name,
		        s.total_index AS totalIndex
		    FROM outdoor_spots os
		    JOIN scuba_forecast s ON os.id = s.spot_id
		    WHERE s.forecast_date = :forecastDate
		    ORDER BY
		        CASE s.total_index
					WHEN 'IMPOSSIBLE' THEN -1
					WHEN 'VERY_BAD' THEN (1.0 / 5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) / 1000 + 1)) / 2
					WHEN 'BAD' THEN (2.0 / 5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) / 1000 + 1)) / 2
					WHEN 'NORMAL' THEN (3.0 / 5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) / 1000 + 1)) / 2
					WHEN 'GOOD' THEN (4.0 / 5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) / 1000 + 1)) / 2
					WHEN 'VERY_GOOD' THEN (5.0 / 5 + 1 / (ST_Distance_Sphere(os.geo_point, ST_SRID(POINT(:longitude, :latitude), 4326)) / 1000 + 1)) / 2
		        END DESC
		    LIMIT 1
		""", nativeQuery = true)
	SpotPreviewProjection findBestSpotInScuba(@Param("latitude") double latitude, @Param("longitude") double longitude,
		@Param("forecastDate") LocalDate forecastDate);

}