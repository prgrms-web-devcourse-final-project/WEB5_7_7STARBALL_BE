package sevenstar.marineleisure.member.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.global.enums.MemberStatus;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.global.exception.enums.MemberErrorCode;
import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.member.dto.MemberDetailResponse;
import sevenstar.marineleisure.member.repository.MemberRepository;

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
			.orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

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
	 * @param memberId 회원 ID
	 * @param nickname 새 닉네임
	 * @return 업데이트된 회원 상세 정보 응답 DTO
	 * @throws NoSuchElementException 회원을 찾을 수 없는 경우
	 */
	@Transactional
	public MemberDetailResponse updateMemberNickname(Long memberId, String nickname) {
		log.info("회원 닉네임 업데이트: memberId={}, nickname={}", memberId, nickname);

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

		member.updateNickname(nickname);
		Member updatedMember = memberRepository.save(member);

		return MemberDetailResponse.builder()
			.id(updatedMember.getId())
			.email(updatedMember.getEmail())
			.nickname(updatedMember.getNickname())
			.status(updatedMember.getStatus())
			.latitude(updatedMember.getLatitude())
			.longitude(updatedMember.getLongitude())
			.build();
	}

	/**
	 * 회원의 위치 정보를 업데이트합니다.
	 *
	 * @param memberId 회원 ID
	 * @param latitude 위도
	 * @param longitude 경도
	 * @return 업데이트된 회원 상세 정보 응답 DTO
	 * @throws NoSuchElementException 회원을 찾을 수 없는 경우
	 */
	@Transactional
	public MemberDetailResponse updateMemberLocation(Long memberId, BigDecimal latitude, BigDecimal longitude) {
		log.info("회원 위치 정보 업데이트: memberId={}, latitude={}, longitude={}", memberId, latitude, longitude);

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

		// 위치 정보 업데이트 로직 (Member 클래스에 해당 메서드 추가 필요)
		updateMemberLocationFields(member, latitude, longitude);
		Member updatedMember = memberRepository.save(member);

		return MemberDetailResponse.builder()
			.id(updatedMember.getId())
			.email(updatedMember.getEmail())
			.nickname(updatedMember.getNickname())
			.status(updatedMember.getStatus())
			.latitude(updatedMember.getLatitude())
			.longitude(updatedMember.getLongitude())
			.build();
	}

	/**
	 * 회원의 상태를 업데이트합니다.
	 *
	 * @param memberId 회원 ID
	 * @param status 새 상태
	 * @return 업데이트된 회원 상세 정보 응답 DTO
	 * @throws NoSuchElementException 회원을 찾을 수 없는 경우
	 */
	@Transactional
	public MemberDetailResponse updateMemberStatus(Long memberId, MemberStatus status) {
		log.info("회원 상태 업데이트: memberId={}, status={}", memberId, status);

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

		// 상태 업데이트 로직 (Member 클래스에 해당 메서드 추가 필요)
		updateMemberStatusField(member, status);
		Member updatedMember = memberRepository.save(member);

		return MemberDetailResponse.builder()
			.id(updatedMember.getId())
			.email(updatedMember.getEmail())
			.nickname(updatedMember.getNickname())
			.status(updatedMember.getStatus())
			.latitude(updatedMember.getLatitude())
			.longitude(updatedMember.getLongitude())
			.build();
	}

	/**
	 * 회원을 탈퇴 처리합니다.
	 *
	 * @param memberId 회원 ID
	 * @throws NoSuchElementException 회원을 찾을 수 없는 경우
	 */
	@Transactional
	public void deleteMember(Long memberId) {
		log.info("회원 탈퇴 처리: memberId={}", memberId);

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

		// 회원 상태를 EXPIRED로 변경 (실제 삭제 대신 소프트 삭제 방식 사용)
		updateMemberStatusField(member, MemberStatus.EXPIRED);
		memberRepository.save(member);
	}

	@Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
	@Transactional
	public void deleteExpiredMember() {
		LocalDateTime expired = LocalDateTime.now().minusDays(30);
		try {
			int deleteCnt = memberRepository.deleteByStatusAndUpdatedAtBefore(MemberStatus.EXPIRED, expired);
			log.info("[Scheduler] deleted expired member: count={}", deleteCnt);
		} catch (Exception e) {
			log.error("[Scheduler] failed to delete expired member: {}", e.getMessage());
		}
	}
	/**
	 * 회원의 위치 정보 필드를 업데이트합니다.
	 * 이 메서드는 Member 엔티티에 직접 접근하여 필드를 수정합니다.
	 *
	 * @param member 회원 엔티티
	 * @param latitude 위도
	 * @param longitude 경도
	 */
	private void updateMemberLocationFields(Member member, BigDecimal latitude, BigDecimal longitude) {
		// Member 클래스에 setter가 없으므로 리플렉션 또는 별도의 메서드 필요
		// 현재는 직접 필드에 접근하는 방식으로 구현
		try {
			java.lang.reflect.Field latField = Member.class.getDeclaredField("latitude");
			java.lang.reflect.Field longField = Member.class.getDeclaredField("longitude");

			latField.setAccessible(true);
			longField.setAccessible(true);

			latField.set(member, latitude);
			longField.set(member, longitude);

			latField.setAccessible(false);
			longField.setAccessible(false);
		} catch (Exception e) {
			log.error("회원 위치 정보 업데이트 중 오류 발생", e);
			throw new RuntimeException("회원 위치 정보 업데이트 실패", e);
		}
	}

	/**
	 * 회원의 상태 필드를 업데이트합니다.
	 * 이 메서드는 Member 엔티티에 직접 접근하여 필드를 수정합니다.
	 *
	 * @param member 회원 엔티티
	 * @param status 새 상태
	 */
	private void updateMemberStatusField(Member member, MemberStatus status) {
		// Member 클래스에 setter가 없으므로 리플렉션 또는 별도의 메서드 필요
		// 현재는 직접 필드에 접근하는 방식으로 구현
		try {
			java.lang.reflect.Field statusField = Member.class.getDeclaredField("status");

			statusField.setAccessible(true);
			statusField.set(member, status);
			statusField.setAccessible(false);
		} catch (Exception e) {
			log.error("회원 상태 업데이트 중 오류 발생", e);
			throw new RuntimeException("회원 상태 업데이트 실패", e);
		}
	}
}
