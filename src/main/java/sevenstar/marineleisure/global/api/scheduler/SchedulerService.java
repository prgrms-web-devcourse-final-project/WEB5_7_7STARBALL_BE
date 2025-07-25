package sevenstar.marineleisure.global.api.scheduler;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.global.api.kakao.service.PresetSchedulerService;
import sevenstar.marineleisure.global.api.khoa.service.KhoaApiService;
import sevenstar.marineleisure.global.api.openmeteo.dto.service.OpenMeteoService;
import sevenstar.marineleisure.spot.repository.SpotViewQuartileRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulerService {
	public static final int MAX_UPDATE_DAY = 3;
	private final KhoaApiService khoaApiService;
	private final OpenMeteoService openMeteoService;
	private final PresetSchedulerService presetSchedulerService;
	private final SpotViewQuartileRepository spotViewQuartileRepository;


	private final Executor taskExecutor;

	// public SchedulerService(
	// 	KhoaApiService khoaApiService,
	// 	OpenMeteoService openMeteoService,
	// 	PresetSchedulerService presetSchedulerService,
	// 	SpotViewQuartileRepository spotViewQuartileRepository,
	// 	@Qualifier("applicationTaskExecutor") Executor taskExecutor   // ★ 여기
	// ) {
	// 	this.khoaApiService          = khoaApiService;
	// 	this.openMeteoService        = openMeteoService;
	// 	this.presetSchedulerService  = presetSchedulerService;
	// 	this.spotViewQuartileRepository = spotViewQuartileRepository;
	// 	this.taskExecutor            = taskExecutor;
	// }
	/**
	 * 앞으로의 스케줄링 전략에 의해 수정될 부분입니다.
	 * @author guwnoong
	 */
	@Scheduled(initialDelay = 0, fixedDelay = 86400000)
	public void scheduler() {
		LocalDate today = LocalDate.now();
		LocalDate endDate = today.plusDays(MAX_UPDATE_DAY);

		// 1. khoaApiService 먼저 실행 (순차적)
		khoaApiService.updateApi(today, endDate);

		// 2. 나머지 작업들을 병렬로 실행
		CompletableFuture<Void> openMeteoFuture = CompletableFuture.runAsync(() -> {
			openMeteoService.updateApi(today, endDate);
		}, taskExecutor);

		CompletableFuture<Void> presetSchedulerFuture = CompletableFuture.runAsync(() -> {
			presetSchedulerService.updateRegionApi();
		}, taskExecutor);

		CompletableFuture<Void> spotViewQuartileFuture = CompletableFuture.runAsync(() -> {
			spotViewQuartileRepository.upsertQuartile();
		}, taskExecutor);

		// 모든 병렬 작업이 완료될 때까지 기다림
		CompletableFuture.allOf(openMeteoFuture, presetSchedulerFuture, spotViewQuartileFuture).join();

		log.info("=== update data ===");
	}
}
