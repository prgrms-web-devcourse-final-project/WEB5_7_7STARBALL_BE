package sevenstar.marineleisure.meeting.service;

import sevenstar.marineleisure.meeting.Dto.Request.CreateMeetingRequest;
import sevenstar.marineleisure.meeting.Dto.Response.MeetingDetailResponse;
import sevenstar.marineleisure.meeting.Dto.Response.MeetingListResponse;
import sevenstar.marineleisure.member.domain.Member;

/**
 * member 은 공통적으로 CustomMemberDetail 에서 가져온 memberDetail로 변경 예정입니다.
 */
public interface MeetingService {

	/**
	 * 모임 목록 조회
	 * [GET] /meetings
	 * @param member
	 * @param cursorId : cursorId 부터 탐색 합니다.
	 * @param size : 가져올 갯수
	 * @return
	 */
	MeetingListResponse getAllMeetings(Member member,Long cursorId, int size);

	/**
	 * 모임 상세 정보 조회
	 * [GET] /meetings/{id}
	 * @param id : meeting.Id를 받아옵니다.
	 * @return
	 */
	MeetingDetailResponse getMeetingDetails(Long id);

	/**
	 * 내 모임 목록 조회 - 내가 주최한 모임
	 * [GET] /meetings/my/hosted
	 * @param member
	 * @param cursorId : cursorId 부터 탐색 합니다.
	 * @param size : 가져올 갯수
	 * @return
	 */
	MeetingListResponse getHostedMeetings(Member member,Long cursorId, int size);

	/**
	 * 내 모임 목록 조회 - 내가 참여한 모임
	 * [GET] /meetings/my/joined
	 * @param member
	 * @param cursorId : cursorId 부터 탐색 합니다.
	 * @param size : 가져올 갯수
	 * @return
	 */
	MeetingListResponse getJoinedMeetings(Member member,Long cursorId, int size);

	/**
	 * 내 모임 목록 조회 - 끝난 모임
	 * [GET] /meetings/my/end
	 * @param member
	 * @param cursorId : cursorId 부터 탐색 합니다.
	 * @param size : 가져올 갯수
	 * @return
	 */
	MeetingListResponse getEndMeetings(Member member,Long cursorId, int size);

	/**
	 * 모임 개수 조회 - 대시보드용
	 * [GET] /meeting/counts
	 * @param member
	 * @return Count 형식이라서 Long 형태로 넘겨받았습니다.
	 */
	Long countMeetings(Member member);

	/**
	 * 모임참여
	 * [POST] /meeting/{id}
	 * @param meetingId : 현재 참여하는 Id를 줍니다.
	 * @param member
	 * @return meetingId -> 참여한 meetingId 로 넘겨줍니다.
	 */
	Long joinMeeting(Long meetingId, Member member);

	/**
	 * 모임 참여 취소
	 * [DELETE] /meetings/{id}
	 * @param meetingId : MeetingId
	 * @param member
	 */
	void leaveMeeting(Long meetingId,Member member);

	/**
	 * 모임 생성
	 * [POST] /meetings
	 * @param member
	 * @param request : CreateMeetingRequest : VO로 tags를 받지 않기때문에 서비스 로직에서 tags를 따로 DTO에 넣어줘야합니다.
	 * @return Long 형태로 MeetingId를 반환할 것 같습니다.
	 */
	Long createMeeting(Member member, CreateMeetingRequest request);

	/**
	 * 모임 정보 수정
	 * [PUT] /meetings/{id}
	 * @param meetingId : memberId
	 * @param member
	 * @param request :
	 * @return
	 */
	Long updateMeeting(Long meetingId, Member member, CreateMeetingRequest request);

	/**
	 * 모임 해체
	 * [DELETE] /meetings/{id}
	 * @param member
	 * @param meetingId
	 */
	void deleteMeeting(Member member, Long meetingId);
}
