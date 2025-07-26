package sevenstar.marineleisure.spot.domain;

import java.util.List;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sevenstar.marineleisure.global.enums.Region;

public interface RegionBoundaryRepository extends JpaRepository<RegionBoundary, Region> {
	@Query("""
    SELECT r
    FROM RegionBoundary r
    WHERE within(:point, r.geom) = true
    """)
	List<RegionBoundary> findByPointWithin(@Param("point") Point point);

}