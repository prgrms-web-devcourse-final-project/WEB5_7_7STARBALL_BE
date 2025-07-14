package sevenstar.marineleisure.meeting.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sevenstar.marineleisure.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	boolean existsById(Long id);

}
