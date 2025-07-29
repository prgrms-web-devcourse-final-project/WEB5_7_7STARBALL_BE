package sevenstar.marineleisure.meeting.service;

import org.springframework.data.domain.Slice;

import sevenstar.marineleisure.global.enums.MeetingRole;
import sevenstar.marineleisure.global.enums.MeetingStatus;
import sevenstar.marineleisure.meeting.dto.request.CreateMeetingRequest;
import sevenstar.marineleisure.meeting.dto.request.UpdateMeetingRequest;
import sevenstar.marineleisure.meeting.dto.response.GoingMeetingResponse;
import sevenstar.marineleisure.meeting.dto.response.MeetingDetailAndMemberResponse;
import sevenstar.marineleisure.meeting.dto.response.MeetingDetailResponse;
import sevenstar.marineleisure.meeting.domain.Meeting;
import sevenstar.marineleisure.member.domain.Member;

/**
 * member 은 공통적으로 CustomMemberDetail 에서 가져온 memberDetail로 변경 예정입니다.
 */
public interface MeetingService {

	/**
	 * 모임 목록 조회
	 * [GET] /meetings
	 * @param cursorId : cursorId 부터 탐색 합니다.
	 * @param size : 가져올 갯수
	 * @return
	 */
	Slice<Meeting> getAllMeetings(Long cursorId, int size);

	/**
	 * 모임 상세 정보 조회
	 * [GET] /meetings/{id}
	 * @param meetingId : meeting.Id를 받아옵니다.
	 * @return
	 */
	MeetingDetailResponse getMeetingDetails(Long meetingId);

	/**
	 *
	 * @param memberId
	 * @param cursorId
	 * @param size
	 * @param MeetingStatus
	 * @return
	 */
	Slice<Meeting> getStatusMyMeetings_role(Long memberId , MeetingRole role , Long cursorId, int size, MeetingStatus meetingStatus);




	MeetingDetailAndMemberResponse getMeetingDetailAndMember(Long memberId, Long meetingId);

	/**
	 * 모임 개수 조회 - 대시보드용
	 * [GET] /meeting/counts
	 * @param memberId
	 * @return Count 형식이라서 Long 형태로 넘겨받았습니다.
	 */
	Long countMeetings(Long memberId);

	/**
	 * 모임참여
	 * [POST] /meeting/{id}
	 * @param meetingId : 현재 참여하는 Id를 줍니다.
	 * @param memberId
	 * @return meetingId -> 참여한 meetingId 로 넘겨줍니다.
	 */
	Long joinMeeting(Long meetingId, Long memberId);

	/**
	 * 모임 참여 취소
	 * [DELETE] /meetings/{id}
	 * @param meetingId : MeetingId
	 * @param memberId
	 */
	void leaveMeeting(Long meetingId,Long memberId);

	/**
	 * 모임 생성
	 * [POST] /meetings
	 * @param memberId
	 * @param request : CreateMeetingRequest : VO로 tags를 받지 않기때문에 서비스 로직에서 tags를 따로 DTO에 넣어줘야합니다.
	 * @return Long 형태로 MeetingId를 반환할 것 같습니다.
	 */
	Long createMeeting(Long memberId, CreateMeetingRequest request);

	/**
	 * 모임 정보 수정
	 * [PUT] /meetings/{id}
	 * @param meetingId : memberId
	 * @param memberId
	 * @param request :
	 * @return
	 */
	Long updateMeeting(Long meetingId, Long memberId, UpdateMeetingRequest request);

	/**
	 * 모임 해체
	 * [DELETE] /meetings/{id}
	 * @param member
	 * @param meetingId
	 */
	void deleteMeeting(Member member, Long meetingId);

	GoingMeetingResponse goingMeeting(Long meetingId, Long memberId);
}
