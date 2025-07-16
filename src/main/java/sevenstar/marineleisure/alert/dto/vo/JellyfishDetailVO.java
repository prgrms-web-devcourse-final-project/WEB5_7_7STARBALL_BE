package sevenstar.marineleisure.alert.dto.vo;

import java.time.LocalDate;

public interface JellyfishDetailVO {
	String getSpecies();

	String getRegion();

	String getDensityType();

	String getToxicity();

	LocalDate getReportDate();
}
