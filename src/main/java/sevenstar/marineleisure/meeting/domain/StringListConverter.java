package sevenstar.marineleisure.meeting.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.AttributeConverter;

public class StringListConverter implements AttributeConverter<List<String>, String> {
	private static final String SPLIT_CHAR = ",";

	@Override
	public String convertToDatabaseColumn(List<String> attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute.stream().collect(Collectors.joining(SPLIT_CHAR));
	}

	@Override
	public List<String> convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		}
		return Arrays.stream(dbData.split(SPLIT_CHAR)).toList();
	}
}
