package sevenstar.marineleisure.global.utils;

import java.math.BigDecimal;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.global.enums.Region;
import sevenstar.marineleisure.spot.domain.RegionBoundary;
import sevenstar.marineleisure.spot.domain.RegionBoundaryRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeoUtils {
	private final GeometryFactory geometryFactory;
	private final RegionBoundaryRepository regionBoundaryRepository;

	public Point createPoint(BigDecimal latitude, BigDecimal longitude) {
		return geometryFactory.createPoint(new Coordinate(longitude.doubleValue(), latitude.doubleValue()));
	}

	public Region searchRegion(float latitude, float longitude) {
		List<RegionBoundary> regionBoundaries = regionBoundaryRepository.findByPointWithin(
			geometryFactory.createPoint(new Coordinate(longitude, latitude)));
		if (regionBoundaries.isEmpty() || regionBoundaries.size() > 1) {
			return Region.OCEAN;
		}
		return regionBoundaries.getFirst().getRegion();
	}

}
