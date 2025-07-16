package sevenstar.marineleisure.member.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sevenstar.marineleisure.global.enums.MemberStatus;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

	@Test
	@DisplayName("빌더 패턴을 사용하여 Member 객체를 생성할 수 있다")
	void createMemberWithBuilder() {
		// given
		String nickname = "testUser";
		String email = "test@example.com";
		String provider = "kakao";
		String providerId = "12345";
		BigDecimal latitude = BigDecimal.valueOf(37.5665);
		BigDecimal longitude = BigDecimal.valueOf(126.9780);

		// when
		Member member = Member.builder()
			.nickname(nickname)
			.email(email)
			.provider(provider)
			.providerId(providerId)
			.latitude(latitude)
			.longitude(longitude)
			.build();

		// then
		assertThat(member).isNotNull();
		assertThat(member.getNickname()).isEqualTo(nickname);
		assertThat(member.getEmail()).isEqualTo(email);
		assertThat(member.getProvider()).isEqualTo(provider);
		assertThat(member.getProviderId()).isEqualTo(providerId);
		assertThat(member.getLatitude()).isEqualTo(latitude);
		assertThat(member.getLongitude()).isEqualTo(longitude);
		assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE); // 기본값 확인
	}

	@Test
	@DisplayName("updateNickname 메서드를 사용하여 닉네임을 변경할 수 있다")
	void updateNickname() {
		// given
		Member member = Member.builder()
			.nickname("oldNickname")
			.email("test@example.com")
			.provider("kakao")
			.providerId("12345")
			.build();
		String newNickname = "newNickname";

		// when
		member.updateNickname(newNickname);

		// then
		assertThat(member.getNickname()).isEqualTo(newNickname);
	}

	@Test
	@DisplayName("updateStatus 메서드를 사용하여 회원 상태를 변경할 수 있다")
	void updateStatus() {
		// given
		Member member = Member.builder()
			.nickname("testUser")
			.email("test@example.com")
			.provider("kakao")
			.providerId("12345")
			.build();
		MemberStatus newStatus = MemberStatus.EXPIRED;

		// when
		member.updateStatus(newStatus);

		// then
		assertThat(member.getStatus()).isEqualTo(newStatus);
	}

	@Test
	@DisplayName("updateLocation 메서드를 사용하여 위치 정보를 변경할 수 있다")
	void updateLocation() {
		// given
		Member member = Member.builder()
			.nickname("testUser")
			.email("test@example.com")
			.provider("kakao")
			.providerId("12345")
			.latitude(BigDecimal.valueOf(37.5665))
			.longitude(BigDecimal.valueOf(126.9780))
			.build();
		BigDecimal newLatitude = BigDecimal.valueOf(35.1796);
		BigDecimal newLongitude = BigDecimal.valueOf(129.0756);

		// when
		member.updateLocation(newLatitude, newLongitude);

		// then
		assertThat(member.getLatitude()).isEqualTo(newLatitude);
		assertThat(member.getLongitude()).isEqualTo(newLongitude);
	}

	@Test
	@DisplayName("updateLocation 메서드는 null이 아닌 값만 업데이트한다")
	void updateLocationWithNullValues() {
		// given
		BigDecimal initialLatitude = BigDecimal.valueOf(37.5665);
		BigDecimal initialLongitude = BigDecimal.valueOf(126.9780);
		Member member = Member.builder()
			.nickname("testUser")
			.email("test@example.com")
			.provider("kakao")
			.providerId("12345")
			.latitude(initialLatitude)
			.longitude(initialLongitude)
			.build();

		// when: 위도만 업데이트
		BigDecimal newLatitude = BigDecimal.valueOf(35.1796);
		member.updateLocation(newLatitude, null);

		// then
		assertThat(member.getLatitude()).isEqualTo(newLatitude);
		assertThat(member.getLongitude()).isEqualTo(initialLongitude); // 변경되지 않음

		// when: 경도만 업데이트
		BigDecimal newLongitude = BigDecimal.valueOf(129.0756);
		member.updateLocation(null, newLongitude);

		// then
		assertThat(member.getLatitude()).isEqualTo(newLatitude); // 이전에 변경된 값 유지
		assertThat(member.getLongitude()).isEqualTo(newLongitude);
	}

	@Test
	@DisplayName("Member 객체는 BaseEntity를 상속받아 생성 및 수정 시간 정보를 가진다")
	void memberExtendsBaseEntity() {
		// given
		Member member = Member.builder()
			.nickname("testUser")
			.email("test@example.com")
			.provider("kakao")
			.providerId("12345")
			.build();

		// then
		// BaseEntity의 createdAt과 updatedAt은 JPA 영속화 시점에 설정되므로
		// 단위 테스트에서는 null이 예상됨
		assertThat(member.getCreatedAt()).isNull();
		assertThat(member.getUpdatedAt()).isNull();
	}
}
