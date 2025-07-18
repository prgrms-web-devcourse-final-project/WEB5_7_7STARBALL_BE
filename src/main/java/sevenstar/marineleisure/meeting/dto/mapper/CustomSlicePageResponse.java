package sevenstar.marineleisure.meeting.dto.mapper;

import java.util.List;

import lombok.Getter;

@Getter
public class CustomSlicePageResponse<T> {
	private final List<T> data;
	private final Long cursorId;
	private final Integer size;
	private final boolean hasNext;

	public CustomSlicePageResponse(List<T> data, Long cursorId, Integer size, boolean hasNext) {
		this.data = data;
		this.cursorId = cursorId;
		this.size = size;
		this.hasNext = hasNext;
	}
}
