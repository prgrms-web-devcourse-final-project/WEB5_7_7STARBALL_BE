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
		"SELECT m "
			+ "FROM Meeting m "
			+ "ORDER BY m.createdAt DESC, m.id DESC"
	)
	Slice<Meeting> findAllByOrderByCreatedAtDescIdDesc(Pageable pageable);

	@Query("SELECT m FROM Meeting m WHERE (m.createdAt < :createdAt OR (m.createdAt = :createdAt AND m.id < :meetingId)) ORDER BY m.createdAt DESC, m.id DESC")
	Slice<Meeting> findAllOrderByCreatedAt(@Param("createdAt") LocalDateTime createdAt, @Param("meetingId") Long meetingId, Pageable pageable);

	@Query("SELECT m FROM Meeting m WHERE m.hostId = :memberId AND m.status = :status AND m.id < :cursorId ORDER BY m.id DESC")
	Slice<Meeting> findMyMeetingsByMemberIdAndStatusWithCursor(@Param("memberId") Long memberId, @Param("status") MeetingStatus status, @Param("cursorId") Long cursorId, Pageable pageable);

	@Query("SELECT COUNT(m) FROM Meeting m WHERE m.hostId = :memberId")
	Long countMyMeetingsByMemberId(@Param("memberId") Long memberId);





}
