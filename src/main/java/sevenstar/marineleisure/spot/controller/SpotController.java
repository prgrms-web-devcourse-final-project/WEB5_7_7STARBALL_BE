package sevenstar.marineleisure.spot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.global.domain.BaseResponse;
import sevenstar.marineleisure.spot.dto.SpotPreviewReadResponse;
import sevenstar.marineleisure.spot.dto.SpotPreviewRequest;
import sevenstar.marineleisure.spot.dto.SpotReadRequest;
import sevenstar.marineleisure.spot.dto.SpotReadResponse;
import sevenstar.marineleisure.spot.dto.detail.SpotDetailReadResponse;
import sevenstar.marineleisure.spot.service.SpotService;

@RestController
@RequestMapping("/map/spots")
@RequiredArgsConstructor
public class SpotController {
	private final SpotService spotService;

	@GetMapping
	ResponseEntity<BaseResponse<SpotReadResponse>> getSpots(@ModelAttribute @Valid SpotReadRequest request) {
		return BaseResponse.success(
			spotService.searchSpot(request.getLatitude(), request.getLongitude(), request.getRadius(),
				request.getCategory()));
	}

	@GetMapping("/{id}")
	ResponseEntity<BaseResponse<SpotDetailReadResponse>> getSpotDetail(@PathVariable Long id) {
		spotService.upsertSpotViewStats(id);
		return BaseResponse.success(spotService.searchSpotDetail(id));
	}

	@GetMapping("/preview")
	ResponseEntity<BaseResponse<SpotPreviewReadResponse>> getSpotPreview(
		@ModelAttribute @Valid SpotPreviewRequest request) {
		return BaseResponse.success(spotService.preview(request.getLatitude(), request.getLongitude()));
	}
}
