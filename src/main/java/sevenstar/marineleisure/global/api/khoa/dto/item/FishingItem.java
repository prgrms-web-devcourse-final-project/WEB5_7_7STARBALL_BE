package sevenstar.marineleisure.global.api.khoa.dto.item;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.utils.DateUtils;

@Getter
@NoArgsConstructor
public class FishingItem implements KhoaItem {
	private String seafsPstnNm;
	private double lat;
	private double lot;
	private String predcYmd;
	private String predcNoonSeCd;
	private String seafsTgfshNm;
	private int tdlvHrScr;
	private float minWvhgt;
	private float maxWvhgt;
	private float minWtem;
	private float maxWtem;
	private float minArtmp;
	private float maxArtmp;
	private float minCrsp;
	private float maxCrsp;
	private float minWspd;
	private float maxWspd;
	private String totalIndex;
	private double lastScr;

	@Override
	public String getLocation() {
		return seafsPstnNm;
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
		return ActivityCategory.FISHING;
	}

	@Override
	public LocalDate getForecastDate() {
		return DateUtils.parseDate(predcYmd);
	}
}
