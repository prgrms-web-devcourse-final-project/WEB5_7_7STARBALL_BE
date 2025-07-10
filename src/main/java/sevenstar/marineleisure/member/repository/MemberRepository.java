package sevenstar.marineleisure.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sevenstar.marineleisure.member.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
//    Optional<Member> findByUserNickname(String username);
    Optional<Member> findByProviderAndProviderId(String provider, String providerId);
}
