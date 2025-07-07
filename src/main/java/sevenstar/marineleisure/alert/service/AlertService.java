package sevenstar.marineleisure.alert.service;

import java.util.List;

public interface AlertService<T> {

	/**
	 * 위험요소 발생 목록 조회
	 *[GET] /alerts
	 * 추가 기능으로 적조도 분석도 구현할 가능성이 있기에, 제네릭으로 두었습니다.
	 * @return 지역별 워험요소 발생 목록
	 */
	public List<T> search();
}
