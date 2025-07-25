package sevenstar.marineleisure.meeting.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.global.enums.MeetingRole;
import sevenstar.marineleisure.global.enums.MeetingStatus;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.meeting.error.MeetingError;
import sevenstar.marineleisure.meeting.error.ParticipantError;
import sevenstar.marineleisure.meeting.repository.ParticipantRepository;
import sevenstar.marineleisure.meeting.domain.Meeting;
import sevenstar.marineleisure.meeting.domain.Participant;
import sevenstar.marineleisure.meeting.domain.Tag;
import sevenstar.marineleisure.meeting.dto.mapper.MeetingMapper;
import sevenstar.marineleisure.meeting.dto.request.CreateMeetingRequest;
import sevenstar.marineleisure.meeting.dto.request.UpdateMeetingRequest;
import sevenstar.marineleisure.meeting.dto.response.MeetingDetailAndMemberResponse;
import sevenstar.marineleisure.meeting.dto.response.MeetingDetailResponse;
import sevenstar.marineleisure.meeting.dto.response.ParticipantResponse;
import sevenstar.marineleisure.meeting.dto.mapper.MeetingMapper;
import sevenstar.marineleisure.meeting.repository.MeetingRepository;
import sevenstar.marineleisure.meeting.repository.ParticipantRepository;
import sevenstar.marineleisure.meeting.repository.TagRepository;
import sevenstar.marineleisure.meeting.domain.Meeting;
import sevenstar.marineleisure.meeting.domain.Participant;
import sevenstar.marineleisure.meeting.domain.Tag;
import sevenstar.marineleisure.meeting.validate.MeetingValidate;
import sevenstar.marineleisure.meeting.validate.MemberValidate;
import sevenstar.marineleisure.meeting.validate.ParticipantValidate;
import sevenstar.marineleisure.meeting.validate.SpotValidate;
import sevenstar.marineleisure.meeting.validate.TagValidate;
import sevenstar.marineleisure.meeting.domain.service.MeetingDomainService;
import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.member.repository.MemberRepository;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.repository.OutdoorSpotRepository;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {
	private final MeetingRepository meetingRepository;
	private final ParticipantRepository participantRepository;
	private final TagRepository tagRepository;
	private final MemberRepository memberRepository;
	private final OutdoorSpotRepository outdoorSpotSpotRepository;
	private final ParticipantValidate participantValidate;
	private final MeetingMapper meetingMapper;
	private final MeetingValidate meetingValidate;
	private final MemberValidate memberValidate;
	private final TagValidate tagValidate;
	private final SpotValidate spotValidate;
	private final MeetingDomainService meetingDomainService;

	@Override
	@Transactional(readOnly = true)
	//TODO : 카테고리 별로 확인 하는 방법 고민하기?
	public Slice<Meeting> getAllMeetings(Long cursorId, int size) {
		Pageable pageable = PageRequest.of(0, size);
		if (cursorId == 0L) {
			return meetingRepository.findAllByOrderByCreatedAtDescIdDesc(pageable);
		} else {
			Meeting meeting = meetingValidate.foundMeeting(cursorId);
			return meetingRepository.findAllOrderByCreatedAt(meeting.getCreatedAt(), meeting.getId(), pageable);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public MeetingDetailResponse getMeetingDetails(Long meetingId) {
		//TODO : select 세번 해야하는것에 대한 개선점 찾기 -> JOIN 패치를 진행 하기로 맘을 먹었음
		//TODO : 그럼에도 JPA 매핑과 JOIN에 대한 속도차이같은걸 조금 알면 좋을듯?
		Meeting targetMeeting = meetingValidate.foundMeeting(meetingId);
		Member host = memberValidate.foundMember(targetMeeting.getHostId());
		OutdoorSpot targetSpot = spotValidate.foundOutdoorSpot(targetMeeting.getSpotId());
		Tag targetTag = tagValidate.findByMeetingId(meetingId).orElse(null);

		return meetingMapper.MeetingDetailResponseMapper(targetMeeting, host, targetSpot, targetTag);
	}

	@Override
	@Transactional(readOnly = true)
	public Slice<Meeting> getStatusMyMeetings_role(Long memberId ,MeetingRole role , Long cursorId, int size, MeetingStatus meetingStatus) {
		Pageable pageable = PageRequest.of(0, size);
		memberValidate.existMember(memberId);
		Long currentCursorId = (cursorId == null || cursorId == 0L) ? Long.MAX_VALUE : cursorId;
		return meetingRepository.findMeetingsByParticipantRoleWithCursor(
			memberId,
			meetingStatus,
			role,
			currentCursorId,
			pageable
		);
	}

	@Override
	@Transactional(readOnly = true)
	public MeetingDetailAndMemberResponse getMeetingDetailAndMember(Long memberId , Long meetingId){
		Member host = memberValidate.foundMember(memberId);
		Meeting targetMeeting = meetingValidate.foundMeeting(meetingId);
		if (!targetMeeting.isHost(host.getId())) {
			throw new IllegalArgumentException("Only host can access member details");
		}
		OutdoorSpot targetSpot = spotValidate.foundOutdoorSpot(targetMeeting.getSpotId());
		List<Participant> participants = participantRepository.findParticipantsByMeetingId(meetingId);
		participantValidate.existParticipant(memberId);
		List<Long> participantUserIds = participants.stream()
			.map(Participant::getUserId)
			.toList();
		Map<Long,String> participantNicknames = memberRepository.findAllById(participantUserIds).stream()
			.collect(Collectors.toMap(Member::getId, Member::getNickname));
		Tag targetTag = tagValidate.findByMeetingId(meetingId).orElse(null);
		List<ParticipantResponse> participantResponseList = meetingMapper.toParticipantResponseList(participants,participantNicknames);
		return meetingMapper.meetingDetailAndMemberResponseMapper(targetMeeting,host,targetSpot,participantResponseList,targetTag);
	}

	@Override
	@Transactional(readOnly = true)
	public Long countMeetings(Long memberId) {
		memberValidate.existMember(memberId);
		return meetingRepository.countMyMeetingsByMemberId(memberId);
	}

	@Override
	@Transactional
	public Long joinMeeting(Long meetingId, Long memberId) {
		memberValidate.existMember(memberId);
		Meeting meeting = meetingValidate.foundMeeting(meetingId);
		
		// 도메인 서비스를 통해 참가자 추가
		meetingDomainService.addParticipant(meeting, memberId, MeetingRole.GUEST);
		
		// 미팅 상태가 변경되었을 수 있으므로 저장
		meetingRepository.save(meeting);
		
		return meetingId;
	}

	@Override
	@Transactional
	public void leaveMeeting(Long meetingId, Long memberId) {
		memberValidate.existMember(memberId);
		Meeting meeting = meetingValidate.foundMeeting(meetingId);
		
		// 도메인 서비스를 통해 참가자 제거
		meetingDomainService.removeParticipant(meeting, memberId);
		
		// 미팅 상태가 변경되었을 수 있으므로 저장
		meetingRepository.save(meeting);
	}

	@Override
	@Transactional
	public Long createMeeting(Long memberId, CreateMeetingRequest request) {
		Member host = memberValidate.foundMember(memberId);
		Meeting saveMeeting = meetingRepository.save(meetingMapper.CreateMeeting(request, host.getId()));
		Participant hostParticipant = Participant.builder()
			.meetingId(saveMeeting.getId())
			.userId(host.getId())
			.role(MeetingRole.HOST)
			.build();
		participantRepository.save(hostParticipant);
		tagRepository.save(
			meetingMapper.saveTag(saveMeeting.getId(), request)
		);

		return saveMeeting.getId();
	}

	//어떻게 해야할지 고민을 해야할 것 같습니다.
	@Override
	@Transactional
	public Long updateMeeting(Long meetingId, Long memberId, UpdateMeetingRequest request) {
		Member host = memberValidate.foundMember(memberId);
		Meeting targetMeeting = meetingValidate.foundMeeting(meetingId);
		
		if (!targetMeeting.isHost(host.getId())) {
			throw new IllegalArgumentException("Only host can update meeting");
		}
		
		targetMeeting.updateMeetingInfo(
			request.title(),
			request.description(),
			request.localDateTime(),
			request.capacity()
		);
		
		Tag targetTag = tagValidate.findByMeetingId(meetingId).orElse(null);
		Meeting updateMeeting = meetingRepository.save(targetMeeting);
		tagRepository.save(
			meetingMapper.UpdateTag(request, targetTag)
		);
		return updateMeeting.getId();
	}
	// 프론트분한테 물어보기 대작전 해야할듯
	//삭제 할 필요가 있을까? 고민해봐야할것같음.
	@Override
	public void deleteMeeting(Member member, Long meetingId) {

	}
}
