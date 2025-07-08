package sevenstar.marineleisure.global.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.experimental.UtilityClass;

/**
 * 날짜 관련 유틸리티 클래스입니다.
 * <p>
 * 날짜를 특정 형식으로 포맷하거나, 날짜 범위를 생성하는 등의 기능을 제공합니다.
 * @author gunwoong
 */
@UtilityClass
public class DateUtils {
	private static final DateTimeFormatter REQ_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
	private static final DateTimeFormatter FORECAST_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

	public static String formatTime(LocalDate localDate) {
		return localDate.format(REQ_DATE_FORMATTER);
	}

	/**
	 * 특정 날짜를 기준으로 date format 변경
	 */
	public static LocalDate parseDate(String date) {
		return LocalDate.parse(date, FORECAST_DATE_FORMATTER);
	}

	public static String formatTime(LocalTime time) {
		return time.format(DATE_TIME_FORMATTER);

	}

}
