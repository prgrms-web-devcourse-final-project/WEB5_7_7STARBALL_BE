package sevenstar.marineleisure.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sevenstar.marineleisure.member.domain.Member;

import java.util.Optional;
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
//    Optional<Member> findByUserNickname(String username);
    Optional<Member> findByProviderAndProviderId(String provider, String providerId);

    boolean existsById(Long id);
}
