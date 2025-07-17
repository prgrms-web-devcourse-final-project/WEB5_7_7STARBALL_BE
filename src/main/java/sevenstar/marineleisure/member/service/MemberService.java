package sevenstar.marineleisure.member.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.global.enums.MemberStatus;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.global.exception.enums.MemberErrorCode;
import sevenstar.marineleisure.meeting.repository.ParticipantRepository;
import sevenstar.marineleisure.meeting.domain.Meeting;
import sevenstar.marineleisure.meeting.domain.Participant;
import sevenstar.marineleisure.meeting.repository.MeetingRepository;
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
	private final MeetingRepository meetingRepository;
	private final ParticipantRepository participantRepository;

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
	 * 1. 회원이 호스트인 경우 해당 미팅을 삭제합니다.
	 * 2. 회원이 게스트인 경우 참가자 목록에서 삭제합니다.
	 * 3. 회원 상태를 EXPIRED로 변경합니다 (소프트 삭제).
	 *
	 * @param memberId 회원 ID
	 * @throws NoSuchElementException 회원을 찾을 수 없는 경우
	 */
	@Transactional
	public void deleteMember(Long memberId) {
		log.info("회원 탈퇴 처리: memberId={}", memberId);

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

		// 1. 회원이 호스트인 경우 해당 미팅을 삭제
		List<Meeting> hostedMeetings = meetingRepository.findByHostId(memberId);
		if (!hostedMeetings.isEmpty()) {
			log.info("호스트로 등록된 미팅 삭제: memberId={}, meetingCount={}", memberId, hostedMeetings.size());
			meetingRepository.deleteAll(hostedMeetings);
		}

		// 2. 회원이 게스트인 경우 참가자 목록에서 삭제
		List<Participant> participations = participantRepository.findByUserId(memberId);
		if (!participations.isEmpty()) {
			log.info("참가자 목록에서 삭제: memberId={}, participationCount={}", memberId, participations.size());
			participantRepository.deleteAll(participations);
		}

		// 3. 회원 상태를 EXPIRED로 변경 (실제 삭제 대신 소프트 삭제 방식 사용)
		updateMemberStatusField(member, MemberStatus.EXPIRED);
		memberRepository.save(member);

		log.info("회원 탈퇴 처리 완료: memberId={}", memberId);
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
	 * 회원의 위치 정보를 업데이트합니다.
	 * 이 메서드는 Member 엔티티의 updateLocation 메서드를 사용합니다.
	 *
	 * @param member 회원 엔티티
	 * @param latitude 위도
	 * @param longitude 경도
	 */
	private void updateMemberLocationFields(Member member, BigDecimal latitude, BigDecimal longitude) {
		member.updateLocation(latitude, longitude);
	}

	/**
	 * 회원의 상태를 업데이트합니다.
	 * 이 메서드는 Member 엔티티의 updateStatus 메서드를 사용합니다.
	 *
	 * @param member 회원 엔티티
	 * @param status 새 상태
	 */
	private void updateMemberStatusField(Member member, MemberStatus status) {
		member.updateStatus(status);
	}
}