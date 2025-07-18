package sevenstar.marineleisure.global.api.khoa.dto.item;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.utils.DateUtils;

@Getter
@NoArgsConstructor
public class SurfingItem implements KhoaItem {
	private String surfPlcNm;
	private double lat;
	private double lot;
	private String predcYmd;
	private String predcNoonSeCd;
	private String avgWvhgt;
	private String avgWvpd;
	private String avgWspd;
	private String avgWtem;
	private String totalIndex;
	private double lastScr;

	@Override
	public String getLocation() {
		return surfPlcNm;
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
		return ActivityCategory.SURFING;
	}

	@Override
	public LocalDate getForecastDate() {
		return DateUtils.parseDate(predcYmd);
	}
}
