package sevenstar.marineleisure.global.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

	/**
	 * 현재 날짜를 기준으로 지정된 일수만큼의 날짜 리스트를 생성합니다.
	 *
	 * @param days 생성할 날짜의 개수(오늘 포함)
	 * @return 지정된 일수만큼의 날짜 리스트
	 */
	public static List<String> getRangeDateListFromNow(int days) {
		LocalDate today = LocalDate.now();

		return IntStream.range(0, days)
			.mapToObj(i -> String.format("%s00", today.plusDays(i).format(REQ_DATE_FORMATTER)))
			.collect(Collectors.toList());
	}

}
