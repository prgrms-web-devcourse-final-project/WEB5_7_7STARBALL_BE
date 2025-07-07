package sevenstar.marineleisure.alert.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sevenstar.marineleisure.global.domain.BaseEntity;
import sevenstar.marineleisure.global.enums.ToxicityLevel;

@Entity
@Table(name = "jellyfish_species")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JellyfishSpecies extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 20)
	private String name;

	@Column(nullable = false)
	private ToxicityLevel toxicity;

	@Builder
	public JellyfishSpecies(String name, ToxicityLevel toxicity) {
		this.name = name;
		this.toxicity = toxicity;
	}
}