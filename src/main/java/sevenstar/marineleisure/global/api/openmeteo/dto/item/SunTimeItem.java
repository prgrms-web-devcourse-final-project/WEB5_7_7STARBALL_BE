package sevenstar.marineleisure.global.api.openmeteo.dto.item;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;

@Getter
public class SunTimeItem {
	private List<LocalDate> time;
	private List<LocalDateTime> sunrise;
	private List<LocalDateTime> sunset;

	public SunTimeItem(List<LocalDate> time, List<LocalDateTime> sunrise, List<LocalDateTime> sunset) {
		this.time = time;
		this.sunrise = sunrise;
		this.sunset = sunset;
	}
}
