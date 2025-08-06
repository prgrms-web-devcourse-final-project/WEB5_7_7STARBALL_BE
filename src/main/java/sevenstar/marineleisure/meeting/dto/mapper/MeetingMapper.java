package sevenstar.marineleisure.meeting.dto.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import sevenstar.marineleisure.global.enums.MeetingRole;
import sevenstar.marineleisure.global.enums.MeetingStatus;
import sevenstar.marineleisure.meeting.domain.Participant;
import sevenstar.marineleisure.meeting.dto.request.CreateMeetingRequest;
import sevenstar.marineleisure.meeting.dto.request.UpdateMeetingRequest;
import sevenstar.marineleisure.meeting.domain.Meeting;
import sevenstar.marineleisure.meeting.domain.Tag;
import sevenstar.marineleisure.meeting.dto.response.MeetingDetailAndMemberResponse;
import sevenstar.marineleisure.meeting.dto.response.MeetingDetailResponse;
import sevenstar.marineleisure.meeting.dto.response.ParticipantResponse;
import sevenstar.marineleisure.meeting.dto.vo.DetailSpot;
import sevenstar.marineleisure.meeting.dto.vo.TagList;
import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;

@Component
public class MeetingMapper {
	// Rich Domain Model 리팩토링으로 불필요해진 메서드들
	// Meeting.changeStatus()로 대체됨
	/*
	public Meeting UpdateStatus(Meeting meeting, MeetingStatus status) {
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
	*/

	public Meeting CreateMeeting(CreateMeetingRequest request, Long hostId) {
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

	// Meeting.updateMeetingInfo()로 대체됨
	/*
	public Meeting UpdateMeeting(UpdateMeetingRequest request, Meeting meeting) {
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
	*/

	public Tag UpdateTag(UpdateMeetingRequest request, Tag tag) {
		return
			//Tag 매퍼를 써야함
			Tag.builder()
				.id(tag.getId())
				.meetingId(tag.getMeetingId())
				.content(request.tag().content() != null ? request.tag().content() : tag.getContent())
				.build();
	}

	public MeetingDetailResponse MeetingDetailResponseMapper(Meeting targetMeeting, Member host,Integer currentParticipant,
		OutdoorSpot targetSpot, Tag targetTag) {
		return MeetingDetailResponse.builder()
			.id(targetMeeting.getId())
			.title(targetMeeting.getTitle())
			.category(targetMeeting.getCategory())
			.capacity(targetMeeting.getCapacity())
			.currentParticipants(currentParticipant)
			.hostId(targetMeeting.getHostId())
			.hostNickName(host.getNickname())
			.hostEmail(host.getEmail())
			.description(targetMeeting.getDescription())
			.spot(DetailSpot.builder()
				.id(targetSpot.getId())
				.name(targetSpot.getName())
				.location(targetSpot.getLocation())
				.build())
			.meetingTime(targetMeeting.getMeetingTime())
			.status(targetMeeting.getStatus())
			.createdAt(targetMeeting.getCreatedAt())
			.tag(targetTag != null ? TagList.builder().content(targetTag.getContent()).build() : TagList.builder().content(Collections.emptyList()).build())
			.build();
	}

	public MeetingDetailAndMemberResponse meetingDetailAndMemberResponseMapper
		(Meeting targetMeeting, Member host, OutdoorSpot targetSpot,
			List<ParticipantResponse> participantResponseList
		, Tag tag) {
		return MeetingDetailAndMemberResponse.builder()
			.id(targetMeeting.getId())
			.title(targetMeeting.getTitle())
			.category(targetMeeting.getCategory())
			.capacity(targetMeeting.getCapacity())
			.hostId(targetMeeting.getHostId())
			.hostNickName(host.getNickname())
			.spot(
				DetailSpot.builder()
					.id(targetMeeting.getSpotId())
					.name(targetSpot.getName())
					.location(targetSpot.getLocation())
					.latitude(targetSpot.getLatitude())
					.longitude(targetSpot.getLongitude())
					.build()
			)
			.meetingTime(targetMeeting.getMeetingTime())
			.status(targetMeeting.getStatus())
			.participants(
				participantResponseList
			)
			.createdAt(targetMeeting.getCreatedAt())
			.tagList(DetailTag(tag))
			.build();
	}

	public List<ParticipantResponse> toParticipantResponseList(List<Participant> participants,
		Map<Long, String> participantNicknames) {
		if (participants == null || participants.isEmpty()) {
			return Collections.emptyList();
		}
		return participants.stream()
			.map(participant -> ParticipantResponse.builder()
				.id(participant.getUserId())
				.role(participant.getRole())
				.nickName(participantNicknames.get(participant.getUserId()))
				.build())
			.toList();

	}

	// Meeting.addParticipant()에서 직접 생성으로 대체됨
	/*
	public Participant saveParticipant(Long memberId,Long meetingId,MeetingRole role){
		return Participant.builder()
			.meetingId(meetingId)
			.userId(memberId)
			.role(role)
			.build();
	}
	*/

	public  Tag saveTag(Long meetingId, CreateMeetingRequest request){
		return Tag.builder()
			.meetingId(meetingId)
			.content(request.tags())
			.build();
	}

	public TagList DetailTag(Tag tag){
		return TagList.builder()
			.content(tag.getContent())
			.build();
	}
}
