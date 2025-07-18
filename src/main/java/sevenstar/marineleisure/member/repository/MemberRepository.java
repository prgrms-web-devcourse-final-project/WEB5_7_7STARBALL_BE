package sevenstar.marineleisure.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sevenstar.marineleisure.global.enums.MemberStatus;
import org.springframework.stereotype.Repository;

import sevenstar.marineleisure.member.domain.Member;

import java.time.LocalDateTime;
import java.util.Optional;
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
//    Optional<Member> findByUserNickname(String username);
    Optional<Member> findByProviderAndProviderId(String provider, String providerId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
		DELETE FROM Member m
				WHERE m.status = :status
						AND m.updatedAt < :expired
		""")
    int deleteByStatusAndUpdatedAtBefore(@Param("status") MemberStatus memberStatus,
        @Param("expired") LocalDateTime expired);

    boolean existsById(Long id);
}
