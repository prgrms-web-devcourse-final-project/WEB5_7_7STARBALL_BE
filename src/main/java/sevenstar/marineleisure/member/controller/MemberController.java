package sevenstar.marineleisure.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sevenstar.marineleisure.global.domain.BaseResponse;
import sevenstar.marineleisure.global.util.CurrentUserUtil;
import sevenstar.marineleisure.member.dto.MemberDetailResponse;
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
}
