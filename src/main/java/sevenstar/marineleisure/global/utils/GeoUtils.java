package sevenstar.marineleisure.global.utils;

import java.math.BigDecimal;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.global.api.kakao.KakaoApiClient;
import sevenstar.marineleisure.global.enums.Region;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeoUtils {
	private final GeometryFactory geometryFactory;
	private final KakaoApiClient kakaoApiClient;

	public Point createPoint(BigDecimal latitude, BigDecimal longitude) {
		return geometryFactory.createPoint(new Coordinate(longitude.doubleValue(), latitude.doubleValue()));
	}

	public Region searchRegion(float latitude, float longitude) {
		return Region.fromAddress(kakaoApiClient.get(latitude, longitude).getBody().getDocuments().getFirst()
			.getAddress_name());
	}

}
