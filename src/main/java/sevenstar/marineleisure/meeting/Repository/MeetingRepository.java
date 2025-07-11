package sevenstar.marineleisure.meeting.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sevenstar.marineleisure.global.enums.MeetingStatus;
import sevenstar.marineleisure.meeting.domain.Meeting;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

	@Query(
		"SELECT m"
			+ "FROM Meeting m "
			+ "ORDER BY m.createdAt DESC, m.id DESC"
	)
	Slice<Meeting> findAllByOrderByCreatedAtDescIdDesc(Pageable pageable);

	@Query(
		"SELECT m " +
			"FROM Meeting m " +
			"WHERE (m.createdAt < :createdAt OR (m.createdAt = :createdAt AND m.id <: meetingID)) " +
			"ORDER BY m.createdAt DESC, m.id DESC"
	)
	Slice<Meeting> findAllOrderByCreatedAt(@Param("createdAt") LocalDateTime createdAt,
		@Param("meetingId") Long meetingId, Pageable pageable);

	@Query("SELECT m FROM Meeting m JOIN Participant p ON m.id = p.meetingId "
		+ "WHERE p.userId = :userId "
		+ "AND m.status = :status "
		+ "AND m.id < :cursorId "
		+ "ORDER BY m.id DESC")
	Slice<Meeting> findMyMeetingsByMemberIdAndStatusWithCursor(
		@Param("userId") Long userId,
		@Param("staus") MeetingStatus status,
		@Param("cursorId") Long cursorId,
		Pageable pageable
	);

	@Query("SELECT count(m) FROM Meeting m JOIN Participant p ON m.id = p.meetingId "
		+ "WHERE p.userId = :userId ")
	Long countMyMeetingsByMemberId(Long userId);





}
