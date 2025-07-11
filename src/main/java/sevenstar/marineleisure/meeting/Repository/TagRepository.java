package sevenstar.marineleisure.meeting.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import sevenstar.marineleisure.meeting.domain.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
	Optional<Tag> findByMeetingId(Long meetingId);

	@Query("SELECT t.content FROM Tag t WHERE t.meetingId = :meetingId")
	List<String> findContentsByMeetingId(Long meetingId);
}
