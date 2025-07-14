package sevenstar.marineleisure.global.api.khoa.dto.item;

import java.math.BigDecimal;
import java.time.LocalDate;

import sevenstar.marineleisure.global.enums.ActivityCategory;

public interface KhoaItem {
	String getLocation();

	BigDecimal getLatitude();

	BigDecimal getLongitude();

	ActivityCategory getCategory();

	LocalDate getForecastDate();
}