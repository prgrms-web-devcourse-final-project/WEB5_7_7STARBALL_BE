package sevenstar.marineleisure.meeting.validate;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.meeting.repository.TagRepository;
import sevenstar.marineleisure.meeting.domain.Tag;

@Component
@RequiredArgsConstructor
public class TagValidate {

	private final TagRepository tagRepository;

	@Transactional(readOnly = true)
	public Optional<Tag> findByMeetingId(Long meetingId){
		return tagRepository.findByMeetingId(meetingId);
	}
}
