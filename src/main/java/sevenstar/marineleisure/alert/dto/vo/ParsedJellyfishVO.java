package sevenstar.marineleisure.alert.dto.vo;

/**
 * AI를 이용해 뽑아온 데이터 입니다.
 * @param species : 종이름
 * @param region : 출현지역
 * @param densityType : 출현 밀도
 */
public record ParsedJellyfishVO(
	String species,
	String region,
	String densityType) {

}
