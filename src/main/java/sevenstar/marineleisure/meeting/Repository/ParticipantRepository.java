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

	@Query(
		"SELECT  p.meetingId "
			+ "FROM Participant  p "
			+ "WHERE p.userId = :memberId "
			+ "ORDER BY p.createdAt , p.id DESC "

	)
	Slice<Long> findAllOrderByStatusAllFirst(Pageable pageable,@Param("memberId") Long memberId);

	@Query("SELECT p.meetingId FROM Participant p JOIN Meeting m ON p.meetingId = m.id " +
		            "WHERE p.userId = :userId " +
		            "AND m.status = :status " +
		            "AND m.id < :cursorId " +
		            "ORDER BY m.id DESC")
	List<Long> findMeetingIdsByUserIdAndStatusWithCursor(
            @Param("userId") Long userId,
            @Param("status") MeetingStatus status,
            @Param("cursorId") Long cursorId,
            Pageable pageable
        );

	@Query("SELECT count(*) FROM Participant p WHERE p.meetingId = :meetingId")
	Optional<Integer> countMeetingIdMember(@Param("meetingId") Long meetingId);

	Optional<Participant> findByMeetingIdAndUserId(Long meetingId, Long userId);

	List<ParticipantResponse> findByMeetingId(Long meetingId);
}
