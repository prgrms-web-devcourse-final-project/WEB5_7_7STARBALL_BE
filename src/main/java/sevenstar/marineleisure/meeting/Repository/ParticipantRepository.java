package sevenstar.marineleisure.meeting.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sevenstar.marineleisure.global.enums.MeetingStatus;
import sevenstar.marineleisure.meeting.Dto.Response.ParticipantResponse;
import sevenstar.marineleisure.meeting.domain.Meeting;
import sevenstar.marineleisure.meeting.domain.Participant;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

	// Temporarily disabled for testing
	// @Query(...)
	// Slice<Long> findAllOrderByStatusAllFirst(Pageable pageable, @Param("memberId") Long memberId);

	// Temporarily disabled for testing
	// List<Long> findMeetingIdsByUserIdAndStatusWithCursor(...);

	@Query("SELECT count(*) FROM Participant p WHERE p.meetingId = :meetingId")
	Optional<Integer> countMeetingIdMember(@Param("meetingId") Long meetingId);

	Optional<Participant> findByMeetingIdAndUserId(Long meetingId, Long userId);

	@Query("SELECT p FROM Participant p WHERE p.meetingId = :meetingId")
	List<Participant> findParticipantsByMeetingId(@Param("meetingId") Long meetingId);

	boolean existsByUserId(Long userId);
}
