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
import sevenstar.marineleisure.global.enums.MeetingRole;
import sevenstar.marineleisure.global.enums.MeetingStatus;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.meeting.error.MeetingError;
import sevenstar.marineleisure.meeting.error.ParticipantError;

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
		this.status = status != null ? status : MeetingStatus.RECRUITING;
	}


	public void updateMeetingInfo(String title, String description, LocalDateTime meetingTime, int capacity) {
		validateForUpdate();
		
		this.title = title != null ? title : this.title;
		this.description = description != null ? description : this.description;
		this.meetingTime = meetingTime != null ? meetingTime : this.meetingTime;
		
		if (capacity > 0 && capacity != this.capacity) {
			this.capacity = capacity;
		}
	}

	public void changeStatus(MeetingStatus newStatus) {
		validateStatusChange(newStatus);
		this.status = newStatus;
	}

	public boolean isHost(Long userId) {
		return this.hostId.equals(userId);
	}

	public boolean canJoin() {
		return this.status == MeetingStatus.RECRUITING;
	}

	public boolean canLeave() {
		return this.status != MeetingStatus.COMPLETED && this.status != MeetingStatus.ONGOING;
	}


	private void validateForUpdate() {
		if (this.status == MeetingStatus.COMPLETED || this.status == MeetingStatus.ONGOING) {
			throw new CustomException(MeetingError.CANNOT_UPDATE_COMPLETED_MEETING);
		}
	}

	private void validateStatusChange(MeetingStatus newStatus) {
		if (this.status == MeetingStatus.COMPLETED && newStatus != MeetingStatus.COMPLETED) {
			throw new CustomException(MeetingError.CANNOT_CHANGE_COMPLETED_STATUS);
		}
	}

}
