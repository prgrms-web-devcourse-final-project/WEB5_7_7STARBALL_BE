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
    @DisplayName("update 메서드를 사용하여 닉네임을 변경할 수 있다")
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
        Member updatedMember = member.update(newNickname);

        // then
        assertThat(updatedMember).isSameAs(member); // 동일한 객체 참조 확인
        assertThat(updatedMember.getNickname()).isEqualTo(newNickname);
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