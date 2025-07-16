package sevenstar.marineleisure.meeting.dto.vo;

import java.util.List;

import lombok.Builder;

@Builder
public record TagList(
    List<String> content
) {

}
