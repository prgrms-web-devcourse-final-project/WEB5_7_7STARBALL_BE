package sevenstar.marineleisure.spot.domain;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpotViewStatsId implements Serializable {
	private Long spotId;
	private LocalDate viewDate;
}

