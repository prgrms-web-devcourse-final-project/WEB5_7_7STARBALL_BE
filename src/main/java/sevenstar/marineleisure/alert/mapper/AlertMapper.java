package sevenstar.marineleisure.alert.mapper;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.alert.dto.response.JellyfishResponse;

@Component
@RequiredArgsConstructor
public class AlertMapper {

	public JellyfishResponse toResponseDto(LocalDate reportDate, Map<String, Set<String>> map) {
		return new JellyfishResponse(reportDate, map);
	}
}
