package sevenstar.marineleisure.meeting.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.MeetingStatus;
import sevenstar.marineleisure.meeting.dto.vo.ListSpot;
import sevenstar.marineleisure.meeting.dto.vo.TagList;
import sevenstar.marineleisure.meeting.domain.Meeting;
import sevenstar.marineleisure.meeting.domain.Tag;
import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;

@Builder
public record MeetingListResponse(
	long id,
	String title,
	ActivityCategory category,
	Integer capacity,
	long currentParticipants,
	long hostId,
	String hostNickName,
	LocalDateTime meetingTime,
	MeetingStatus status,
	ListSpot spot,
	TagList tag
) {
	public static MeetingListResponse fromEntity(Meeting meeting , Member host, Long participantCount, OutdoorSpot spot,
		Tag tag){
		return MeetingListResponse.builder()
			.id(meeting.getId())
			.title(meeting.getTitle())
			.category(meeting.getCategory())
			.capacity(meeting.getCapacity())
			.currentParticipants(participantCount)
			.hostId(meeting.getHostId())
			.hostNickName(host.getNickname())
			.meetingTime(meeting.getMeetingTime())
			.status(meeting.getStatus())
			.spot(ListSpot.builder()
				.id(spot.getId())
				.name(spot.getName())
				.location(spot.getLocation())
				.build())
			.tag(TagList.builder()
				.content(
					tag.getContent()
				)
				.build()
			)
			.build();

	}

}
