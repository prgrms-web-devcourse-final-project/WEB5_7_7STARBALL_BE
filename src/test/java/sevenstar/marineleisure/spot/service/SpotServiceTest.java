package sevenstar.marineleisure.spot.service;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import sevenstar.marineleisure.forecast.domain.Fishing;
import sevenstar.marineleisure.forecast.domain.FishingTarget;
import sevenstar.marineleisure.forecast.domain.Mudflat;
import sevenstar.marineleisure.forecast.domain.Scuba;
import sevenstar.marineleisure.forecast.domain.Surfing;
import sevenstar.marineleisure.forecast.repository.FishingRepository;
import sevenstar.marineleisure.forecast.repository.FishingTargetRepository;
import sevenstar.marineleisure.forecast.repository.MudflatRepository;
import sevenstar.marineleisure.forecast.repository.ScubaRepository;
import sevenstar.marineleisure.forecast.repository.SurfingRepository;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.TidePhase;
import sevenstar.marineleisure.global.enums.TimePeriod;
import sevenstar.marineleisure.global.enums.TotalIndex;
import sevenstar.marineleisure.global.utils.GeoUtils;
import sevenstar.marineleisure.spot.config.GeoConfig;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.dto.SpotReadResponse;
import sevenstar.marineleisure.spot.repository.OutdoorSpotRepository;

@DataJpaTest
@Import({SpotServiceImpl.class, GeoUtils.class, GeoConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class SpotServiceTest {
	@Autowired
	private SpotService spotService;
	@Autowired
	private OutdoorSpotRepository outdoorSpotRepository;
	@Autowired
	private FishingRepository fishingRepository;
	@Autowired
	private FishingTargetRepository fishingTargetRepository;
	@Autowired
	private ScubaRepository scubaRepository;
	@Autowired
	private MudflatRepository mudflatRepository;
	@Autowired
	private SurfingRepository surfingRepository;
	@Autowired
	private GeoUtils geoUtils;

	private float baseLat = 37.5503f;
	private float baseLon = 126.9971f;
	@BeforeEach
	void setUp() {
		LocalDate startDate = LocalDate.now();
		LocalDate endDate = startDate.plusDays(7);
		FishingTarget target = fishingTargetRepository.save(new FishingTarget("감성돔"));

		for (ActivityCategory category : List.of(ActivityCategory.FISHING, ActivityCategory.MUDFLAT,
			ActivityCategory.SURFING, ActivityCategory.SCUBA)) {

			// 0.001 ~ 0.005 사이 랜덤한 변화값 생성
			float latOffset = (float) ((Math.random() - 0.5) * 0.01); // ±0.005
			float lonOffset = (float) ((Math.random() - 0.5) * 0.01); // ±0.005

			BigDecimal latitude = BigDecimal.valueOf(baseLat + latOffset);
			BigDecimal longitude = BigDecimal.valueOf(baseLon + lonOffset);
			OutdoorSpot outdoorSpot = outdoorSpotRepository.save(OutdoorSpot.builder()
				.latitude(latitude)
				.longitude(longitude)
				.location("서울특별시 강남구")
				.name("서울특별시 강남구")
				.category(category)
				.point(geoUtils.createPoint(latitude, longitude))
				.build());

			if (category == ActivityCategory.FISHING) {
				for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
					fishingRepository.save(
						Fishing.builder()
							.spotId(outdoorSpot.getId())
							.targetId(target.getId())
							.forecastDate(date)
							.timePeriod(TimePeriod.AM)
							.tide(TidePhase.SPRING_TIDE)
							.totalIndex(TotalIndex.GOOD)
							.build()
					);
				}
			} else if (category == ActivityCategory.SCUBA) {
				for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
					scubaRepository.save(
						Scuba.builder()
							.spotId(outdoorSpot.getId())
							.forecastDate(date)
							.timePeriod(TimePeriod.AM)
							.tide(TidePhase.SPRING_TIDE)
							.totalIndex(TotalIndex.GOOD)
							.build()
					);
				}
			} else if (category == ActivityCategory.SURFING) {
				for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
					surfingRepository.save(
						Surfing.builder()
							.spotId(outdoorSpot.getId())
							.forecastDate(date)
							.timePeriod(TimePeriod.AM)
							.totalIndex(TotalIndex.GOOD)
							.build()
					);
				}
			} else if (category == ActivityCategory.MUDFLAT) {
				for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
					mudflatRepository.save(
						Mudflat.builder()
							.spotId(outdoorSpot.getId())
							.forecastDate(date)
							.totalIndex(TotalIndex.GOOD)
							.build()
					);
				}

			}

		}
	}

	@Test
	void should_searchSpot_when_givenLatitudeAndLongitudeAndActivityCategory() {
		// when
		SpotReadResponse fishingResponse = spotService.searchSpot(baseLat, baseLon,
			ActivityCategory.FISHING);
		SpotReadResponse scubaResponse = spotService.searchSpot(baseLat, baseLon,
			ActivityCategory.SCUBA);
		SpotReadResponse surfingResponse = spotService.searchSpot(baseLat, baseLon,
			ActivityCategory.SURFING);
		SpotReadResponse mudflatResponse = spotService.searchSpot(baseLat, baseLon,
			ActivityCategory.MUDFLAT);

		// then
		assertThat(fishingResponse.spots()).hasSize(1);
		assertThat(scubaResponse.spots()).hasSize(1);
		assertThat(surfingResponse.spots()).hasSize(1);
		assertThat(mudflatResponse.spots()).hasSize(1);
	}

	@Test
	void should_searchAllSpots() {
		// given
		float latitude = 35.1731f;
		float longitude = 129.0714f;

		// when
		SpotReadResponse response = spotService.searchAllSpot(latitude, longitude);

		//
		assertThat(response.spots()).hasSize(4);
	}

}
