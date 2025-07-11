package sevenstar.marineleisure.meeting.Dto.VO;

import java.util.List;

import lombok.Builder;

@Builder
public record TagList(
    List<String> content
) {

}
