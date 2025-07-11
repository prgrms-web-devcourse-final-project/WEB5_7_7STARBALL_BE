package sevenstar.marineleisure.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.member.dto.MemberDetailResponse;
import sevenstar.marineleisure.member.repository.MemberRepository;

import java.util.NoSuchElementException;

/**
 * 회원 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final MemberRepository memberRepository;

	/**
	 * 회원 ID로 회원 상세 정보를 조회합니다.
	 *
	 * @param memberId 회원 ID
	 * @return 회원 상세 정보 응답 DTO
	 * @throws NoSuchElementException 회원을 찾을 수 없는 경우
	 */
	public MemberDetailResponse getMemberDetail(Long memberId) {
		log.info("회원 상세 정보 조회: memberId={}", memberId);

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다: " + memberId));

		return MemberDetailResponse.builder()
			.id(member.getId())
			.email(member.getEmail())
			.nickname(member.getNickname())
			.status(member.getStatus())
			.latitude(member.getLatitude())
			.longitude(member.getLongitude())
			.build();
	}

	/**
	 * 현재 로그인한 회원의 상세 정보를 조회합니다.
	 * 이 메서드는 CurrentUserUtil을 통해 현재 인증된 사용자의 ID를 가져와 사용합니다.
	 *
	 * @param memberId 회원 ID
	 * @return 회원 상세 정보 응답 DTO
	 * @throws NoSuchElementException 회원을 찾을 수 없는 경우
	 */
	public MemberDetailResponse getCurrentMemberDetail(Long memberId) {
		log.info("현재 로그인한 회원 상세 정보 조회: memberId={}", memberId);
		return getMemberDetail(memberId);
	}


	/**
	 * 회원의 닉네임을 업데이트합니다.
	 *
	 * @param memberId 업데이트할 회원 ID
	 * @param nickname 새 닉네임
	 * @return 업데이트된 회원
	 * @throws NoSuchElementException 회원을 찾을 수 없는 경우
	 */
	@Transactional
	public Member updateMemberNickname(Long memberId, String nickname) {
		log.info("회원 닉네임 업데이트: memberId={}, nickname={}", memberId, nickname);

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다: " + memberId));

		member.update(nickname);
		return memberRepository.save(member);
	}
}