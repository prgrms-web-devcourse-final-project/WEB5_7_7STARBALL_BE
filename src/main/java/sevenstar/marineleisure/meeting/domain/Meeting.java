package sevenstar.marineleisure.meeting.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
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
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.MeetingStatus;

@Entity
@Getter
@Table(name = "meetings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Meeting extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 20, nullable = false)
	private String title;

	@Column(nullable = false)
	private ActivityCategory category;

	@Column(nullable = false)
	private int capacity;

	@Column(name = "host_id", nullable = false)
	private Long hostId;

	@Column(name = "meeting_time", nullable = false)
	private LocalDateTime meetingTime;

	@Column(nullable = false)
	private MeetingStatus status;

	@Column(name = "spot_id", nullable = false)
	private Long spotId;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Builder
	public Meeting(LocalDateTime meetingTime, ActivityCategory category, int capacity, Long hostId, String title,
		Long spotId, String description, MeetingStatus status) {
		this.meetingTime = meetingTime;
		this.category = category;
		this.capacity = capacity;
		this.hostId = hostId;
		this.title = title;
		this.spotId = spotId;
		this.description = description;
		this.status = status;
	}

}
