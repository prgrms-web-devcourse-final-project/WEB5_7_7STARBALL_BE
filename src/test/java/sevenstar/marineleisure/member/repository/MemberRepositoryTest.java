package sevenstar.marineleisure.member.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import sevenstar.marineleisure.annotation.H2DataJpaTest;
import sevenstar.marineleisure.member.domain.Member;

@H2DataJpaTest
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

		// when
		Member foundMember = memberRepository.findById(savedMember.getId()).orElseThrow();
		foundMember.update("newNickname");
		memberRepository.save(foundMember);
		entityManager.flush();
		entityManager.clear();

		// then
		Member updatedMember = memberRepository.findById(savedMember.getId()).orElseThrow();
		assertThat(updatedMember.getNickname()).isEqualTo("newNickname");
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