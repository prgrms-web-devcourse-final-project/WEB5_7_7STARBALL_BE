package sevenstar.marineleisure.alert.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.alert.domain.JellyfishRegionDensity;
import sevenstar.marineleisure.alert.dto.response.JellyfishResponseDto;
import sevenstar.marineleisure.alert.mapper.AlertMapper;
import sevenstar.marineleisure.alert.service.JellyfishService;
import sevenstar.marineleisure.global.domain.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alerts")
public class AlertController {
	private final JellyfishService jellyfishService;
	private final AlertMapper alertMapper;

	@GetMapping("/jellyfish")
	public ResponseEntity<BaseResponse<JellyfishResponseDto>> getJellyfishList() {
		List<JellyfishRegionDensity> items = jellyfishService.search();

		JellyfishResponseDto result = null;
		return BaseResponse.success(result);
	}

	@GetMapping("/jellyfish/crawl")
	public ResponseEntity<String> triggerCrawl() {
		jellyfishService.updateLatestReport();
		return ResponseEntity.ok("해파리 리포트 크롤링 완료");
	}
}
