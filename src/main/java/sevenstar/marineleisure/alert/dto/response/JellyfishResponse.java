package sevenstar.marineleisure.alert.dto.response;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JellyfishResponse {
	private LocalDate reposrtDate;
	private Map<String, Set<String>> jellyfish;
}
