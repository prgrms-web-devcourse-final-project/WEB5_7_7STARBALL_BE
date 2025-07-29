package sevenstar.marineleisure.meeting.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.global.domain.BaseResponse;
import sevenstar.marineleisure.global.enums.MeetingRole;
import sevenstar.marineleisure.global.enums.MeetingStatus;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.global.jwt.UserPrincipal;
import sevenstar.marineleisure.meeting.dto.mapper.CustomSlicePageResponse;
import sevenstar.marineleisure.meeting.dto.request.CreateMeetingRequest;
import sevenstar.marineleisure.meeting.dto.request.UpdateMeetingRequest;
import sevenstar.marineleisure.meeting.dto.response.GoingMeetingResponse;
import sevenstar.marineleisure.meeting.dto.response.MeetingDetailAndMemberResponse;
import sevenstar.marineleisure.meeting.dto.response.MeetingDetailResponse;
import sevenstar.marineleisure.meeting.dto.response.MeetingListResponse;

import sevenstar.marineleisure.meeting.repository.ParticipantRepository;
import sevenstar.marineleisure.meeting.repository.TagRepository;
import sevenstar.marineleisure.meeting.domain.Meeting;
import sevenstar.marineleisure.meeting.domain.Tag;
import sevenstar.marineleisure.meeting.error.MeetingError;
import sevenstar.marineleisure.meeting.service.MeetingService;
import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.member.repository.MemberRepository;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.repository.OutdoorSpotRepository;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MeetingController {
	private final MeetingService meetingService;
	// N+1 문제를 발생시키기 위해 모든 관련 Repository를 주입받습니다.
	private final MemberRepository memberRepository;
	private final OutdoorSpotRepository outdoorSpotRepository;
	private final TagRepository tagRepository;
	private final ParticipantRepository participantRepository;

	@GetMapping("/meetings")
	public ResponseEntity<BaseResponse<CustomSlicePageResponse<MeetingListResponse>>> getAllListMeetings(
		@RequestParam(name = "cursorId", defaultValue = "0") Long cursorId,
		@RequestParam(name = "size", defaultValue = "10") Integer size
	) {
		Slice<Meeting> not_mapping_result = meetingService.getAllMeetings(cursorId, size);
		List<Meeting> meetingList = not_mapping_result.getContent();

		// 🚀 Map Batch 최적화로 N+1 문제 해결! (5개 쿼리만)
		// 1. 모든 ID 수집
		Set<Long> hostIds = meetingList.stream().map(Meeting::getHostId).collect(Collectors.toSet());
		Set<Long> spotIds = meetingList.stream().map(Meeting::getSpotId).collect(Collectors.toSet());
		List<Long> meetingIds = meetingList.stream().map(Meeting::getId).collect(Collectors.toList());

		// 2. Batch 조회 (5개 쿼리만!)
		Map<Long, Member> hostMap = memberRepository.findAllById(hostIds)
			.stream().collect(Collectors.toMap(Member::getId, m -> m));

		Map<Long, OutdoorSpot> spotMap = outdoorSpotRepository.findAllById(spotIds)
			.stream().collect(Collectors.toMap(OutdoorSpot::getId, s -> s));

		Map<Long, Tag> tagMap = tagRepository.findByMeetingIdIn(meetingIds)
			.stream().collect(Collectors.toMap(Tag::getMeetingId, t -> t));

		Map<Long, Long> participantCountMap = participantRepository.countByMeetingIdIn(meetingIds)
			.stream().collect(Collectors.toMap(
				result -> (Long) result[0],      // meetingId
				result -> (Long) result[1]       // count
			));

		// 3. 메모리에서 조합 (추가 쿼리 없음!)
		List<MeetingListResponse> dtoList = meetingList.stream()
			.map(meeting -> {
				Member host = hostMap.get(meeting.getHostId());
				OutdoorSpot spot = spotMap.get(meeting.getSpotId());
				Tag tag = tagMap.get(meeting.getId());
				Long participantCount = participantCountMap.getOrDefault(meeting.getId(), 0L);

				// Null 체크 (기존 예외 처리 유지)
				if (host == null) {
					throw new RuntimeException("Host not found for meeting id: " + meeting.getId());
				}
				if (spot == null) {
					throw new RuntimeException("Spot not found for meeting id: " + meeting.getId());
				}
				if (tag == null) {
					throw new CustomException(MeetingError.MEETING_NOT_FOUND);
				}

				return MeetingListResponse.fromEntity(meeting, host, participantCount, spot, tag);
			})
			.collect(Collectors.toList());

		Long nextCursorId = null;
		if(not_mapping_result.hasNext() && !not_mapping_result.getContent().isEmpty()) {
			Meeting lastMeetingInSlice = not_mapping_result.getContent().get(size - 1);
			nextCursorId = lastMeetingInSlice.getId();
		}
		CustomSlicePageResponse<MeetingListResponse> result_Mapping =
			new CustomSlicePageResponse<>(
				dtoList,
				nextCursorId,
				size,
				not_mapping_result.hasNext()
			);
		return BaseResponse.success(result_Mapping);
	}

	@GetMapping("/meetings/{id}")
	public ResponseEntity<BaseResponse<MeetingDetailResponse>> getMeetingDetail(
		@PathVariable("id") Long meetingId
	){
		return BaseResponse.success(meetingService.getMeetingDetails(meetingId));
	}
	@GetMapping("/meetings/my")
	public ResponseEntity<BaseResponse<CustomSlicePageResponse<MeetingListResponse>>> getStatusListMeeting(
		@RequestParam(name = "status",defaultValue = "RECRUITING") MeetingStatus status,
		@RequestParam(name = "role",defaultValue = "GUEST") MeetingRole role,
		@RequestParam(name = "cursorId", defaultValue = "0") Long cursorId,
		@RequestParam(name = "size", defaultValue = "10") Integer size,
		@AuthenticationPrincipal UserPrincipal userDetails
	){

		Long memberId = userDetails.getId();
		Slice<Meeting> not_mapping_result = meetingService.getStatusMyMeetings_role(memberId,role,cursorId,size,status);
		List<Meeting> meetingList = not_mapping_result.getContent();

		// 🚀 Map Batch 최적화로 N+1 문제 해결! (5개 쿼리만)
		// 1. 모든 ID 수집
		Set<Long> hostIds = meetingList.stream().map(Meeting::getHostId).collect(Collectors.toSet());
		Set<Long> spotIds = meetingList.stream().map(Meeting::getSpotId).collect(Collectors.toSet());
		List<Long> meetingIds = meetingList.stream().map(Meeting::getId).collect(Collectors.toList());

		// 2. Batch 조회 (5개 쿼리만!)
		Map<Long, Member> hostMap = memberRepository.findAllById(hostIds)
			.stream().collect(Collectors.toMap(Member::getId, m -> m));

		Map<Long, OutdoorSpot> spotMap = outdoorSpotRepository.findAllById(spotIds)
			.stream().collect(Collectors.toMap(OutdoorSpot::getId, s -> s));

		Map<Long, Tag> tagMap = tagRepository.findByMeetingIdIn(meetingIds)
			.stream().collect(Collectors.toMap(Tag::getMeetingId, t -> t));

		Map<Long, Long> participantCountMap = participantRepository.countByMeetingIdIn(meetingIds)
			.stream().collect(Collectors.toMap(
				result -> (Long) result[0],      // meetingId
				result -> (Long) result[1]       // count
			));

		// 3. 메모리에서 조합 (추가 쿼리 없음!)
		List<MeetingListResponse> dtoList = meetingList.stream()
			.map(meeting -> {
				Member host = hostMap.get(meeting.getHostId());
				OutdoorSpot spot = spotMap.get(meeting.getSpotId());
				Tag tag = tagMap.get(meeting.getId());
				Long participantCount = participantCountMap.getOrDefault(meeting.getId(), 0L);

				// Null 체크 (기존 예외 처리 유지)
				if (host == null) {
					throw new RuntimeException("Host not found for meeting id: " + meeting.getId());
				}
				if (spot == null) {
					throw new RuntimeException("Spot not found for meeting id: " + meeting.getId());
				}
				if (tag == null) {
					throw new CustomException(MeetingError.MEETING_NOT_FOUND);
				}

				return MeetingListResponse.fromEntity(meeting, host, participantCount, spot, tag);
			})
			.collect(Collectors.toList());

		Long nextCursorId = null;
		if(not_mapping_result.hasNext() && !not_mapping_result.getContent().isEmpty()) {
			Meeting lastMeetingInSlice = not_mapping_result.getContent().get(size - 1);
			nextCursorId = lastMeetingInSlice.getId();
		}
		CustomSlicePageResponse<MeetingListResponse> result_Mapping =
			new CustomSlicePageResponse<>(
				dtoList,
				nextCursorId,
				size,
				not_mapping_result.hasNext()
			);
		return BaseResponse.success(result_Mapping);
	}
	@GetMapping("/meetings/count")
	public ResponseEntity<BaseResponse<Long>> countMeetings(@AuthenticationPrincipal UserPrincipal userDetails){
		Long memberId = userDetails.getId();
		return BaseResponse.success(meetingService.countMeetings(memberId));
	}
	@GetMapping("/meetings/{id}/members")
	public ResponseEntity<BaseResponse<MeetingDetailAndMemberResponse>> getMeetingDetailAndMember(
		@PathVariable("id") Long meetingId,
		@AuthenticationPrincipal UserPrincipal userDetails
	){
		Long memberId = userDetails.getId();
		return BaseResponse.success(meetingService.getMeetingDetailAndMember(memberId,meetingId));
	}
	@PostMapping("/meetings/{id}/join")
	public ResponseEntity<BaseResponse<Long>> joinMeeting(
		@PathVariable("id") Long meetingId,
		@AuthenticationPrincipal UserPrincipal userDetails
	){
		Long memberId = userDetails.getId();
		Long result = meetingService.joinMeeting(meetingId,memberId);
		return BaseResponse.success(HttpStatus.CREATED, result);
	}
	@DeleteMapping("/meetings/{id}/leave")
	public ResponseEntity<BaseResponse<String>> leaveMeeting(
		@PathVariable("id") Long meetingId,
		@AuthenticationPrincipal UserPrincipal userDetails
	){
		Long memberId = userDetails.getId();
		meetingService.leaveMeeting(meetingId,memberId);
		return BaseResponse.success(HttpStatus.NO_CONTENT, "success") ;
	}

	@PostMapping("/meetings")
	public ResponseEntity<BaseResponse<Long>> createMeeting(
		@RequestBody CreateMeetingRequest request,
		@AuthenticationPrincipal UserPrincipal userDetails
	){
		Long memberId = userDetails.getId();
		return BaseResponse.success(HttpStatus.CREATED,meetingService.createMeeting(memberId, request));
	}


	@PutMapping("/meetings/{id}/update")
	public ResponseEntity<BaseResponse<Long>> updateMeeting(
		@PathVariable("id") Long meetingId,
		@RequestBody UpdateMeetingRequest request,
		@AuthenticationPrincipal UserPrincipal userDetails
	){
		Long memberId = userDetails.getId();
		return BaseResponse.success(meetingService.updateMeeting(meetingId, memberId, request));
	}

	@PostMapping("/meetings/{id}/going")
	public ResponseEntity<BaseResponse<GoingMeetingResponse>> goingMeeting(
		@PathVariable("id") Long meetingId,
		@AuthenticationPrincipal UserPrincipal userDetails
	){
		Long memberId = userDetails.getId();
		return BaseResponse.success(meetingService.goingMeeting(meetingId, memberId));
	}


}