package sevenstar.marineleisure.meeting.validate;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.meeting.Repository.TagRepository;
import sevenstar.marineleisure.meeting.domain.Tag;

@Component
@RequiredArgsConstructor
@Transactional
public class TagValidate {

	private final TagRepository tagRepository;

	public Tag foundTag(Long meetingId){
		return tagRepository.findByMeetingId(meetingId).orElse(null);
	}
}
