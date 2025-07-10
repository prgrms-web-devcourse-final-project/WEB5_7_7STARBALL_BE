package sevenstar.marineleisure.spot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.global.domain.BaseResponse;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.spot.dto.SpotCreateRequest;
import sevenstar.marineleisure.spot.dto.SpotDetailReadResponse;
import sevenstar.marineleisure.spot.dto.SpotReadRequest;
import sevenstar.marineleisure.spot.dto.SpotReadResponse;
import sevenstar.marineleisure.spot.service.SpotService;

@RestController
@RequestMapping("/map/spots")
@RequiredArgsConstructor
public class SpotController {
	private final SpotService spotService;

	@GetMapping
	ResponseEntity<BaseResponse<SpotReadResponse>> getSpots(@RequestBody @Valid SpotReadRequest request) {
		if (request.getCategory() == null) {
			return BaseResponse.success(
				spotService.searchAllSpot(request.getLatitude(), request.getLongitude()));
		}

		return BaseResponse.success(
			spotService.searchSpot(request.getLatitude(), request.getLongitude(),
				ActivityCategory.valueOf(request.getCategory())));
	}

	@GetMapping("/{id}")
	ResponseEntity<BaseResponse<SpotDetailReadResponse>> getSpotsByCategory(@PathVariable Long id) {
		spotService.upsertSpotViewStats(id);
		return BaseResponse.success(spotService.searchSpotDetail(id));
	}

	// @GetMapping("/preview")
	// ResponseEntity<> getSpotPreview(@RequestBody @Valid SpotReadRequest request) {
	//
	// 	return BaseResponse.success();
	// }

	@PostMapping
		// TODO : 수정 무조건 필요 (중복)
	ResponseEntity createSpot(@RequestBody SpotCreateRequest request) {
		spotService.createOutdoorSpot(request);
		return BaseResponse.success("success");
	}

}
