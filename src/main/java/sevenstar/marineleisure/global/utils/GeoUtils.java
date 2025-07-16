package sevenstar.marineleisure.global.utils;

import java.math.BigDecimal;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.global.api.kakao.KakaoApiClient;
import sevenstar.marineleisure.global.enums.Region;

@Component
@RequiredArgsConstructor
public class GeoUtils {
	private final GeometryFactory geometryFactory;
	private final KakaoApiClient kakaoApiClient;

	public Point createPoint(BigDecimal latitude, BigDecimal longitude) {
		return geometryFactory.createPoint(new Coordinate(latitude.doubleValue(),longitude.doubleValue()));
	}

	public Region searchRegion(float latitude, float longitude) {
		return Region.fromAddress(kakaoApiClient.get(latitude, longitude).getBody().getDocuments().getFirst()
			.getAddress_name());
	}

	public static double meterToBufferDegree(double meter) {
		return meter / 111_000.0;
	}

	public static double kmToMeter(int km) {
		return km * 1000;
	}


}
