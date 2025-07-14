package sevenstar.marineleisure.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sevenstar.marineleisure.global.domain.BaseResponse;
import sevenstar.marineleisure.global.util.CurrentUserUtil;
import sevenstar.marineleisure.member.dto.MemberDetailResponse;
import sevenstar.marineleisure.member.dto.MemberLocationUpdateRequest;
import sevenstar.marineleisure.member.dto.MemberNicknameUpdateRequest;
import sevenstar.marineleisure.member.dto.MemberStatusUpdateRequest;
import sevenstar.marineleisure.member.service.MemberService;

/**
 * 회원 관련 요청을 처리하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	/**
	 * 현재 로그인한 회원의 상세 정보를 조회합니다.
	 *
	 * @return 회원 상세 정보 응답
	 */
	@GetMapping("/me")
	public ResponseEntity<BaseResponse<MemberDetailResponse>> getCurrentMemberDetail() {
		log.info("현재 로그인한 회원 상세 정보 조회 요청");

		// 현재 인증된 사용자의 ID 가져오기
		Long currentUserId = CurrentUserUtil.getCurrentUserId();

		// 회원 상세 정보 조회
		MemberDetailResponse memberDetail = memberService.getCurrentMemberDetail(currentUserId);

		return BaseResponse.success(memberDetail);
	}

	/**
     * 현재 로그인한 회원의 닉네임을 업데이트합니다.
     *
     * @param request 닉네임 업데이트 요청 DTO
     * @return 업데이트된 회원 상세 정보 응답
     */
    @PutMapping("/me")
    public ResponseEntity<BaseResponse<MemberDetailResponse>> updateMemberNickname(
            @RequestBody MemberNicknameUpdateRequest request) {
        log.info("회원 닉네임 업데이트 요청: {}", request.getNickname());

        // 현재 인증된 사용자의 ID 가져오기
        Long currentUserId = CurrentUserUtil.getCurrentUserId();

        // 닉네임 업데이트
        MemberDetailResponse updatedMember = memberService.updateMemberNickname(currentUserId, request.getNickname());

        return BaseResponse.success(updatedMember);
    }

    /**
     * 현재 로그인한 회원의 위치 정보를 업데이트합니다.
     *
     * @param request 위치 정보 업데이트 요청 DTO
     * @return 업데이트된 회원 상세 정보 응답
     */
    @PutMapping("/me/location")
    public ResponseEntity<BaseResponse<MemberDetailResponse>> updateMemberLocation(
            @RequestBody MemberLocationUpdateRequest request) {
        log.info("회원 위치 정보 업데이트 요청: latitude={}, longitude={}", 
                request.getLatitude(), request.getLongitude());

        // 현재 인증된 사용자의 ID 가져오기
        Long currentUserId = CurrentUserUtil.getCurrentUserId();

        // 위치 정보 업데이트
        MemberDetailResponse updatedMember = memberService.updateMemberLocation(
                currentUserId, request.getLatitude(), request.getLongitude());

        return BaseResponse.success(updatedMember);
    }

    /**
     * 현재 로그인한 회원의 상태를 업데이트합니다.
     *
     * @param request 상태 업데이트 요청 DTO
     * @return 업데이트된 회원 상세 정보 응답
     */
    @PatchMapping("/me/status")
    public ResponseEntity<BaseResponse<MemberDetailResponse>> updateMemberStatus(
            @RequestBody MemberStatusUpdateRequest request) {
        log.info("회원 상태 업데이트 요청: {}", request.getStatus());

        // 현재 인증된 사용자의 ID 가져오기
        Long currentUserId = CurrentUserUtil.getCurrentUserId();

        // 상태 업데이트
        MemberDetailResponse updatedMember = memberService.updateMemberStatus(
                currentUserId, request.getStatus());

        return BaseResponse.success(updatedMember);
    }

    /**
     * 현재 로그인한 회원을 소프트 삭제합니다 (상태를 EXPIRED로 변경).
     * 액세스 토큰을 통해 인증된 사용자만 자신의 계정을 삭제할 수 있습니다.
     *
     * @return 삭제 성공 메시지
     */
    @PostMapping("/delete")
    public ResponseEntity<BaseResponse<String>> deleteMember() {
        // 현재 인증된 사용자의 ID 가져오기
        Long currentUserId = CurrentUserUtil.getCurrentUserId();
        log.info("회원 소프트 삭제 요청: 현재 인증된 사용자 ID={}", currentUserId);

        memberService.deleteMember(currentUserId);

        return BaseResponse.success("회원이 성공적으로 삭제되었습니다.");
    }
}
