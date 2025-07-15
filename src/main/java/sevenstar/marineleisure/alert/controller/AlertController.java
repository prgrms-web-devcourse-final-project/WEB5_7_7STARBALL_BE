// package sevenstar.marineleisure.alert.controller;
//
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// import lombok.RequiredArgsConstructor;
// import sevenstar.marineleisure.alert.service.JellyfishService;
// import sevenstar.marineleisure.global.domain.BaseResponse;
//
// @RestController
// @RequiredArgsConstructor
// @RequestMapping("/alerts")
// public class AlertController {
// 	private final JellyfishService jellyfishService;
// 	private final AlertMapper alertMapper;
//
// 	/**
// 	 * 사용자에게 해파리출현에 관한 정보를 넘겨주기위한 메서드입니다.
// 	 * @return 해파리 발생 관련 정보
// 	 */
// 	@GetMapping("/jellyfish")
// 	public ResponseEntity<BaseResponse<JellyfishResponseDto>> getJellyfishList() {
// 		List<JellyfishDetailVO> items = jellyfishService.search();
// 		JellyfishResponseDto result = alertMapper.toResponseDto(items);
// 		return BaseResponse.success(result);
// 	}
//
// 	// 명시적으로 크롤링작업을 호출하기 위함입니다. 프론트에서 사용하지는 않습니다.
// 	// 동작 테스트 완료했습니다.
// 	// OpenAi Token발생하므로 꼭 필요할때만 사용해주세요.
// 	// @GetMapping("/jellyfish/crawl")
// 	// public ResponseEntity<String> triggerCrawl() {
// 	// 	jellyfishService.updateLatestReport();
// 	// 	return ResponseEntity.ok("해파리 리포트 크롤링 완료");
// 	// }
// }
