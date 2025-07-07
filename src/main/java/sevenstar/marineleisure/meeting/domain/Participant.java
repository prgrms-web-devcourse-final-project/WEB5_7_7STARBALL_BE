package sevenstar.marineleisure.meeting.domain;

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
import sevenstar.marineleisure.global.enums.MeetingRole;

@Entity
@Getter
@Table(name = "meeting_participants")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participant extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "meeting_id", nullable = false)
	private Long meetingId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(length = 20, nullable = false)
	private MeetingRole role;

	@Builder
	public Participant(Long meetingId, Long userId, MeetingRole role) {
		this.meetingId = meetingId;
		this.userId = userId;
		this.role = role;
	}
}
