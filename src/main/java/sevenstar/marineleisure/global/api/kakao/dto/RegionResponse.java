package sevenstar.marineleisure.global.api.kakao.dto;

import java.util.List;

import lombok.Data;

@Data
public class RegionResponse {
	private Meta meta;
	private List<Document> documents;

	@Data
	public static class Meta {
		private int total_count;
	}

	@Data
	public static class Document {
		private String region_type;
		private String code;
		private String address_name;
		private String region_1depth_name;
		private String region_2depth_name;
		private String region_3depth_name;
		private String region_4depth_name;
		private double x;
		private double y;
	}
}
