package sevenstar.marineleisure.global.api.openmeteo.dto.item;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class UvIndexItem {
	private List<LocalDate> time;
	@JsonProperty("uv_index_max")
	private List<Float> uvIndexMax;

	public UvIndexItem(List<LocalDate> time, List<Float> uvIndexMax) {
		this.time = time;
		this.uvIndexMax = uvIndexMax;
	}
}
