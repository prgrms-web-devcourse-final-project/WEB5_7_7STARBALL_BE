package sevenstar.marineleisure.global.api.khoa.dto.item;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.utils.DateUtils;

@Getter
@NoArgsConstructor
public class ScubaItem implements KhoaItem {
	private String skscExpcnRgnNm;     // 체험 지역명
	private double lat;                // 위도
	private double lot;                // 경도
	private String predcYmd;           // 예보 날짜
	private String predcNoonSeCd;      // 오전/오후/일
	private String tdlvHrCn;           // 조위 정보 (소조기/대조기 등)
	private String minWvhgt;           // 최소 파고
	private String maxWvhgt;           // 최대 파고
	private String minCrsp;            // 최소 투명도
	private String maxCrsp;            // 최대 투명도
	private String minWtem;            // 최소 수온
	private String maxWtem;            // 최대 수온
	private String totalIndex;         // 체험 지수
	private double lastScr;            // 점수

	@Override
	public String getLocation() {
		return skscExpcnRgnNm;
	}

	@Override
	public BigDecimal getLatitude() {
		return new BigDecimal(String.valueOf(lat));
	}

	@Override
	public BigDecimal getLongitude() {
		return new BigDecimal(String.valueOf(lot));
	}

	@Override
	public ActivityCategory getCategory() {
		return ActivityCategory.SCUBA;
	}

	@Override
	public LocalDate getForecastDate() {
		return DateUtils.parseDate(predcYmd);
	}
}
