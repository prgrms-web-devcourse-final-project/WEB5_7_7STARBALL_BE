package sevenstar.marineleisure.global.utils;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

class GeoUtilsTest {
	private GeoUtils geoUtils = new GeoUtils(new GeometryFactory(new PrecisionModel(), 4326),null);

	@Test
	void should_success() {
		// given
		BigDecimal latitude = BigDecimal.valueOf(37.5665);
		BigDecimal longitude = BigDecimal.valueOf(126.978);

		// when
		Point point = geoUtils.createPoint(latitude, longitude);

		// then
		assertThat(point).isNotNull();
	}
}