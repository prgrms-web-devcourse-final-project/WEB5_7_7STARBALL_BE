package sevenstar.marineleisure.meeting.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sevenstar.marineleisure.meeting.domain.Participant;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

	@Query("SELECT count(*) FROM Participant p WHERE p.meetingId = :meetingId")
	Optional<Integer> countMeetingId(@Param("meetingId") Long meetingId);

	Optional<Participant> findByMeetingIdAndUserId(Long meetingId, Long userId);

	@Query("SELECT p FROM Participant p WHERE p.meetingId = :meetingId")
	List<Participant> findParticipantsByMeetingId(@Param("meetingId") Long meetingId);

	boolean existsByUserId(Long userId);

	boolean existsByMeetingIdAndUserId(Long meetingId, Long memberId);

	List<Participant> findByUserId(Long memberId);

	@Query("SELECT p.meetingId, COUNT(p) FROM Participant p WHERE p.meetingId IN :meetingIds GROUP BY p.meetingId")
	List<Object[]> countByMeetingIdIn(@Param("meetingIds") List<Long> meetingIds);

	@Modifying
	@Query("DELETE FROM Participant p WHERE p.userId = :userId")
	int deleteByUserId(@Param("userId") Long userId);
}
