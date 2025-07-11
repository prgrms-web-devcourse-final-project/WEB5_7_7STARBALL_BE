package sevenstar.marineleisure.meeting.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.global.enums.MeetingRole;
import sevenstar.marineleisure.global.enums.MeetingStatus;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.meeting.Dto.Request.CreateMeetingRequest;
import sevenstar.marineleisure.meeting.Dto.Request.UpdateMeetingRequest;
import sevenstar.marineleisure.meeting.Dto.Response.MeetingDetailAndMemberResponse;
import sevenstar.marineleisure.meeting.Dto.Response.MeetingDetailResponse;
import sevenstar.marineleisure.meeting.Dto.Response.ParticipantResponse;
import sevenstar.marineleisure.meeting.Dto.VO.DetailSpot;
import sevenstar.marineleisure.meeting.Dto.VO.TagList;
import sevenstar.marineleisure.meeting.Dto.mapper.MeetingMapper;
import sevenstar.marineleisure.meeting.Repository.MeetingRepository;
import sevenstar.marineleisure.meeting.Repository.MemberRepository;
import sevenstar.marineleisure.meeting.Repository.OutdoorSpotSpotRepository;
import sevenstar.marineleisure.meeting.Repository.ParticipantRepository;
import sevenstar.marineleisure.meeting.Repository.TagRepository;
import sevenstar.marineleisure.meeting.domain.Meeting;
import sevenstar.marineleisure.meeting.domain.Participant;
import sevenstar.marineleisure.meeting.domain.Tag;
import sevenstar.marineleisure.meeting.error.MeetingError;
import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingServiceImpl implements MeetingService {
	private final MeetingRepository meetingRepository;
	private final ParticipantRepository participantRepository;
	private final TagRepository tagRepository;
	private final MemberRepository memberRepository;
	private final OutdoorSpotSpotRepository outdoorSpotSpotRepository;

	@Override
	//TODO : 카테고리 별로 확인 하는 방법 고민하기?
	public Slice<Meeting> getAllMeetings(Long cursorId, int size) {
		Pageable pageable = PageRequest.of(0, size);
		if (cursorId == 0L) {
			return meetingRepository.findAllByOrderByCreatedAtDescIdDesc(pageable);
		} else {
			Meeting meeting = foundMeeting(cursorId);
			return meetingRepository.findAllOrderByCreatedAt(meeting.getCreatedAt(), meeting.getId(), pageable);
		}
	}

	@Override
	public MeetingDetailResponse getMeetingDetails(Long meetingId) {
		//TODO : validate 확인
		//TODO : errorCode 확인
		//TODO : select 세번 해야하는것에 대한 개선점 찾기 -> JOIN 패치를 진행 하기로 맘을 먹었음
		//TODO : 그럼에도 JPA 매핑과 JOIN에 대한 속도차이같은걸 조금 알면 좋을듯?
		Meeting targetMeeting = meetingRepository.findById(meetingId)
			.orElseThrow(() -> new CustomException(MeetingError.MEETING_NOT_FOUND));
		Member host = foundMember(targetMeeting.getHostId());
		OutdoorSpot targetSpot = outdoorSpotSpotRepository.findById(targetMeeting.getSpotId())
			.orElseThrow(() -> new CustomException(MeetingError.MEETING_NOT_FOUND));
		//태그가 없는 상황이 올까? 그럼 최소 태그 하나는 붙여달라하는 조건을 붙여야할까?
		Tag targetTag = tagRepository.findByMeetingId(meetingId)
			.orElseThrow(() -> new CustomException(MeetingError.MEETING_NOT_FOUND));


		//TODO : Mapper 설정
		return MeetingDetailResponse.builder()
			.id(targetMeeting.getId())
			.title(targetMeeting.getTitle())
			.category(targetMeeting.getCategory())
			.capacity(targetMeeting.getCapacity())
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
			.tag(TagList.builder()
				.content(targetTag.getContent())
				.build())
			.build();
	}

	@Override
	public Slice<Meeting> getStatusMyMeetings(Long memberId, Long cursorId, int size, MeetingStatus meetingStatus) {
		Pageable pageable = PageRequest.of(0, size);
		existMember(memberId);
		Long currentCursorId = (cursorId == null || cursorId == 0L) ? Long.MAX_VALUE : cursorId;
		return meetingRepository.findMyMeetingsByMemberIdAndStatusWithCursor(memberId, meetingStatus,
			currentCursorId, pageable);
	}

	@Override
	public MeetingDetailAndMemberResponse getMeetingDetailAndMember(Long memberId , Long meetingId){
		Member host = foundMember(memberId);
		Meeting targetMeeting = foundMeeting(meetingId);
		if(!Objects.equals(host.getId(), targetMeeting.getHostId())){
			//일치하지 않는다 잘못된 접근이다. 라는 오류 발생
			throw new CustomException(MeetingError.MEETING_NOT_FOUND);
		}
		OutdoorSpot targetSpot = outdoorSpotSpotRepository.findById(targetMeeting.getSpotId())
			.orElseThrow(() -> new CustomException(MeetingError.MEETING_NOT_FOUND));
		List<ParticipantResponse> participantResponseList = participantRepository.findByMeetingId(meetingId);

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
			.build();
	}

	@Override
	public Long countMeetings(Long memberId) {
		//TODO :: member가 실존한 맴버인지 검증이 필요할까?
		return meetingRepository.countMyMeetingsByMemberId(memberId);
	}

	@Override
	//동시성을 처리해야할 문제가 있음
	public Long joinMeeting(Long meetingId, Long memberId) {
		existMember(memberId);
		Meeting meeting = foundMeeting(meetingId);
		if (meeting.getStatus() != MeetingStatus.ONGOING) {
			//TODO : 참여할 수 없는 모임이라고 에러를 띄워야함
			throw new CustomException(MeetingError.MEETING_NOT_FOUND);
		}
		int targetCount = participantRepository.countMeetingIdMember(meetingId)
			//TODO : 참여자수 가 오류났다는걸 말해줘야합니다.
			.orElseThrow(() -> new CustomException(MeetingError.MEETING_NOT_FOUND));
		if (targetCount >= meeting.getCapacity()) {
			throw new CustomException(MeetingError.MEETING_NOT_FOUND);
		}
		participantRepository.save(
			Participant.builder()
				.meetingId(meetingId)
				.userId(memberId)
				.role(MeetingRole.GUEST)
				.build()
		);
		return meetingId;
	}

	@Override
	public void leaveMeeting(Long meetingId, Long memberId) {
		//TODO : member가 진짜 등록된 맴버인지 확인해야함
		Member targetMember = foundMember(memberId);
		Meeting meeting = foundMeeting(meetingId);
		if (targetMember.getId().equals(meeting.getHostId())) {
			//TODO :: host는 나갈수없음을 말해야합니다..
			throw new CustomException(MeetingError.MEETING_NOT_FOUND);
		}

		if (meeting.getStatus() == MeetingStatus.COMPLETED || meeting.getStatus() == MeetingStatus.ONGOING) {
			// TODO: 취소 처리를 할 수 없는 상태라고 해야 함 (더 적절한 에러 코드 필요)
			throw new CustomException(MeetingError.MEETING_NOT_FOUND);
		}

		Participant targetParticipant = participantRepository.findByMeetingIdAndUserId(meetingId, memberId)
			//TODO  :: Participant Not_FOUND 를 해야함
			.orElseThrow(() -> new CustomException(MeetingError.MEETING_NOT_FOUND));

		participantRepository.delete(targetParticipant);
		if (meeting.getStatus() == MeetingStatus.FULL) {
			meetingRepository.save(MeetingMapper.UpdateStatus(meeting, MeetingStatus.RECRUITING));
		}

	}

	@Override
	public Long createMeeting(Long memberId, CreateMeetingRequest request) {
		Member host = foundMember(memberId);
		Meeting saveMeeting = meetingRepository.save(MeetingMapper.CreateMeeting(request, host.getId()));
		//Mapper 두개 추개
		participantRepository.save(
			Participant.builder()
				.meetingId(saveMeeting.getId())
				.userId(host.getId())
				.role(MeetingRole.HOST)
				.build()
		);
		tagRepository.save(
			Tag.builder()
				.meetingId(saveMeeting.getId())
				.content(request.tags())
				.build()
		);

		return saveMeeting.getId();
	}

	//어떻게 해야할지 고민을 해야할 것 같습니다.
	@Override
	public Long updateMeeting(Long meetingId, Long memberId, UpdateMeetingRequest request) {
		Member host = foundMember(memberId);
		Meeting targetMeeting = foundMeeting(meetingId);
		if (!Objects.equals(targetMeeting.getHostId(), host.getId())) {
			//TODO :: 에러 세분화 ->  호스트만 수정할수있음
			throw new CustomException(MeetingError.MEETING_NOT_FOUND);
		}
		Tag targetTag = tagRepository.findByMeetingId(meetingId)
			//Tag가 없는게 말이 되나? (ㅇㅁㅇ?
			.orElseThrow(() -> new CustomException(MeetingError.MEETING_NOT_FOUND));


		Meeting updateMeeting = meetingRepository.save(MeetingMapper.UpdateMeeting(request, targetMeeting));
		//mapper을 고민
		tagRepository.save(
			MeetingMapper.UpdateTag(request, targetTag)
		);
		return updateMeeting.getId();

	}
	// 프론트분한테 물어보기 대작전 해야할듯
	//삭제 할 필요가 있을까? 고민해봐야할것같음.
	@Override
	public void deleteMeeting(Member member, Long meetingId) {

	}

	//TODO : validate 설정
	public Meeting foundMeeting(Long meetingId) {
		return meetingRepository.findById(meetingId)
			.orElseThrow(() -> new CustomException(MeetingError.MEETING_NOT_FOUND));
	}
	public Member foundMember(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(MeetingError.MEETING_NOT_FOUND));
	}

	public void existMember(Long memberId) {
		if (!memberRepository.existsById(memberId)) {
			throw new CustomException(MeetingError.MEETING_NOT_FOUND);
		}

	}
}
