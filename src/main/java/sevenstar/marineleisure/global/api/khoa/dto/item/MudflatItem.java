package sevenstar.marineleisure.global.api.khoa.dto.item;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.utils.DateUtils;

@Getter
public class MudflatItem implements KhoaItem {
	private String mdftExpcnVlgNm;       // 마을 이름
	private double lat;                  // 위도
	private double lot;                  // 경도
	private String predcYmd;             // 예측 날짜
	private String mdftExprnBgngTm;      // 체험 시작 시간
	private String mdftExprnEndTm;       // 체험 종료 시간
	private String minArtmp;             // 최소 기온
	private String maxArtmp;             // 최대 기온
	private String minWspd;              // 최소 풍속
	private String maxWspd;              // 최대 풍속
	private String weather;              // 날씨
	private String totalIndex;           // 체험지수 등급
	private double lastScr;              // 점수

	@Override
	public String getLocation() {
		return mdftExpcnVlgNm;
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
		return ActivityCategory.MUDFLAT;
	}

	@Override
	public LocalDate getForecastDate() {
		return DateUtils.parseDate(predcYmd);
	}
}
