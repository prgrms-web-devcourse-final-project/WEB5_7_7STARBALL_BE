package sevenstar.marineleisure.global.api.khoa.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.spot.dto.detail.provider.ActivityProvider;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class KhoaApiService {
	private final List<ActivityProvider> detailProviders;

	/**
	 * KHOA API를 통해 스쿠버, 낚시, 갯벌, 서핑 정보를 업데이트합니다.
	 * <p>
	 * 해당 날짜 기준으로 7일치 데이터를 가져오며, 각 카테고리별로 데이터를 저장합니다.
	 */
	@Transactional
	public void updateApi(LocalDate startDate, LocalDate endDate) {
		for (ActivityProvider detailProvider : detailProviders) {
			detailProvider.upsert(startDate, endDate);
		}
	}
}

