package sevenstar.marineleisure.meeting.domain;

import java.util.List;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sevenstar.marineleisure.global.domain.BaseEntity;
import sevenstar.marineleisure.meeting.service.util.StringListConverter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tags")
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "meeting_id", nullable = false)
	private Long meetingId;


	@Convert(converter = StringListConverter.class)
	private List<String> content;


	@Builder
	public Tag(Long meetingId, List<String> content) {
		this.meetingId = meetingId;
		this.content = content;
	}

}
