package sevenstar.marineleisure.observatory.domain;

import java.math.BigDecimal;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sevenstar.marineleisure.global.domain.BaseEntity;
import sevenstar.marineleisure.global.enums.HlCode;

@Entity
@Getter
@Table(name = "observatories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Observatory extends BaseEntity {

	// 관측소의 id는 바다누리 API의 관측소 obs_post_id에서 따왔습니다.
	@Id
	@Column(length = 7)
	private String id;

	@Column(nullable = false)
	private String name;

	@Column(precision = 9, scale = 6, nullable = false)
	private BigDecimal latitude;

	@Column(precision = 9, scale = 6, nullable = false)
	private BigDecimal longitude;

	@Column(name = "hl_code", nullable = false)
	private HlCode hlCode;

	@Column(nullable = false)
	private LocalTime time;

}
