package sevenstar.marineleisure.member.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import sevenstar.marineleisure.global.enums.MemberStatus;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import sevenstar.marineleisure.annotation.H2DataJpaTest;
import sevenstar.marineleisure.member.domain.Member;

<<<<<<< HEAD
@H2DataJpaTest
=======
@DataJpaTest
@EnableJpaAuditing
>>>>>>> c521a0a (fix: 소셜 로그인 재시도 시 닉네임 UNIQUE 제약 위반 오류 발생 (#42))
class MemberRepositoryTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	@DisplayName("Member 엔티티를 저장하고 ID로 조회할 수 있다")
	void saveMemberAndFindById() {
		// given
		Member member = createTestMember("testUser", "test@example.com", "kakao", "12345");

		// when
		Member savedMember = memberRepository.save(member);
		entityManager.flush();
		entityManager.clear();

		// then
		Optional<Member> foundMember = memberRepository.findById(savedMember.getId());
		assertThat(foundMember).isPresent();
		assertThat(foundMember.get().getNickname()).isEqualTo("testUser");
		assertThat(foundMember.get().getEmail()).isEqualTo("test@example.com");
		assertThat(foundMember.get().getProvider()).isEqualTo("kakao");
		assertThat(foundMember.get().getProviderId()).isEqualTo("12345");
		assertThat(foundMember.get().getLatitude().compareTo(BigDecimal.valueOf(37.5665))).isEqualTo(0);
		assertThat(foundMember.get().getLongitude().compareTo(BigDecimal.valueOf(126.9780))).isEqualTo(0);
		assertThat(foundMember.get().getStatus()).isEqualTo(MemberStatus.ACTIVE);
		assertThat(foundMember.get().getCreatedAt()).isNotNull();
		assertThat(foundMember.get().getUpdatedAt()).isNotNull();
	}

	@Test
	@DisplayName("provider와 providerId로 Member를 조회할 수 있다")
	void findByProviderAndProviderId() {
		// given
		Member member = createTestMember("testUser", "test@example.com", "kakao", "12345");
		memberRepository.save(member);
		entityManager.flush();
		entityManager.clear();

		// when
		Optional<Member> foundMember = memberRepository.findByProviderAndProviderId("kakao", "12345");

		// then
		assertThat(foundMember).isPresent();
		assertThat(foundMember.get().getNickname()).isEqualTo("testUser");
		assertThat(foundMember.get().getEmail()).isEqualTo("test@example.com");
		assertThat(foundMember.get().getProvider()).isEqualTo("kakao");
		assertThat(foundMember.get().getProviderId()).isEqualTo("12345");
		assertThat(foundMember.get().getLatitude().compareTo(BigDecimal.valueOf(37.5665))).isEqualTo(0);
		assertThat(foundMember.get().getLongitude().compareTo(BigDecimal.valueOf(126.9780))).isEqualTo(0);
		assertThat(foundMember.get().getStatus()).isEqualTo(MemberStatus.ACTIVE);
	}

	@Test
	@DisplayName("존재하지 않는 provider와 providerId로 조회하면 빈 Optional을 반환한다")
	void findByProviderAndProviderIdNotFound() {
		// given
		Member member = createTestMember("testUser", "test@example.com", "kakao", "12345");
		memberRepository.save(member);
		entityManager.flush();
		entityManager.clear();

		// when
		Optional<Member> foundMember = memberRepository.findByProviderAndProviderId("google", "12345");

		// then
		assertThat(foundMember).isEmpty();
	}

	@Test
	@DisplayName("Member 엔티티를 수정할 수 있다")
	void updateMember() {
		// given
		Member member = createTestMember("oldNickname", "test@example.com", "kakao", "12345");
		Member savedMember = memberRepository.save(member);
		entityManager.flush();
		entityManager.clear();

		// 수정 전 상태 저장
		Member beforeUpdate = memberRepository.findById(savedMember.getId()).orElseThrow();
		var originalUpdatedAt = beforeUpdate.getUpdatedAt();

		// 잠시 대기하여 updatedAt 변경 확인을 위한 시간차 생성
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// when
		Member foundMember = memberRepository.findById(savedMember.getId()).orElseThrow();
		foundMember.updateNickname("newNickname");
		memberRepository.save(foundMember);
		entityManager.flush();
		entityManager.clear();

		// then
		Member updatedMember = memberRepository.findById(savedMember.getId()).orElseThrow();
		assertThat(updatedMember.getNickname()).isEqualTo("newNickname");
		assertThat(updatedMember.getEmail()).isEqualTo("test@example.com");
		assertThat(updatedMember.getProvider()).isEqualTo("kakao");
		assertThat(updatedMember.getProviderId()).isEqualTo("12345");
		assertThat(updatedMember.getUpdatedAt()).isAfter(originalUpdatedAt);
	}

	@Test
	@DisplayName("Member 엔티티를 삭제할 수 있다")
	void deleteMember() {
		// given
		Member member = createTestMember("testUser", "test@example.com", "kakao", "12345");
		Member savedMember = memberRepository.save(member);
		entityManager.flush();
		entityManager.clear();

		// when
		memberRepository.deleteById(savedMember.getId());
		entityManager.flush();
		entityManager.clear();

		// then
		Optional<Member> foundMember = memberRepository.findById(savedMember.getId());
		assertThat(foundMember).isEmpty();
	}

	private Member createTestMember(String nickname, String email, String provider, String providerId) {
		return Member.builder()
			.nickname(nickname)
			.email(email)
			.provider(provider)
			.providerId(providerId)
			.latitude(BigDecimal.valueOf(37.5665))
			.longitude(BigDecimal.valueOf(126.9780))
			.build();
	}
}
