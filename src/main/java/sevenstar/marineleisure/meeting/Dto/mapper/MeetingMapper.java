package sevenstar.marineleisure.meeting.Dto.mapper;

import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.MeetingStatus;
import sevenstar.marineleisure.meeting.Dto.Request.CreateMeetingRequest;
import sevenstar.marineleisure.meeting.Dto.Request.UpdateMeetingRequest;
import sevenstar.marineleisure.meeting.domain.Meeting;
import sevenstar.marineleisure.meeting.domain.Tag;

public class MeetingMapper {
	public static Meeting UpdateStatus(Meeting meeting, MeetingStatus status) {
		return Meeting.builder()
			.id(meeting.getId())
			.title(meeting.getTitle())
			.category(meeting.getCategory())
			.capacity(meeting.getCapacity())
			.hostId(meeting.getHostId())
			.meetingTime(meeting.getMeetingTime())
			.status(status)
			.spotId(meeting.getSpotId())
			.description(meeting.getDescription())
			.build();
	}

	public static Meeting CreateMeeting(CreateMeetingRequest request, Long hostId) {
		return Meeting.builder()
			.title(request.title())
			.category(request.category())
			.capacity(request.capacity())
			.hostId(hostId)
			.meetingTime(request.meetingTime())
			.status(MeetingStatus.RECRUITING)
			.spotId(request.spotId())
			.description(request.description())
			.build();
	}

	public static Meeting UpdateMeeting(UpdateMeetingRequest request, Meeting meeting) {
		return
			Meeting.builder()
				.id(meeting.getId())
				.title(request.title() != null ? request.title() : meeting.getTitle())
				.category(request.category() != null ? request.category() : meeting.getCategory())
				.capacity(request.capacity() != null ? request.capacity() : meeting.getCapacity())
				.hostId(meeting.getHostId())
				.meetingTime(request.localDateTime() != null ? request.localDateTime() : meeting.getMeetingTime())
				.status(meeting.getStatus())
				.spotId(request.spotId() != null ? request.spotId() : meeting.getSpotId())
				.description(request.description() != null ? request.description() : meeting.getDescription())
				.build();

	}
	public static Tag UpdateTag(UpdateMeetingRequest request, Tag tag) {
		return
			//Tag 매퍼를 써야함
			Tag.builder()
				.id(tag.getId())
				.meetingId(tag.getMeetingId())
				.content(request.tag().content() != null ? request.tag().content() : tag.getContent())
				.build();
	}

}
