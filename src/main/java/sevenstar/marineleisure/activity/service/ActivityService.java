// package sevenstar.marineleisure.activity.service;
//
// import java.math.BigDecimal;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
//
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
//
// import lombok.RequiredArgsConstructor;
// import sevenstar.marineleisure.activity.dto.reponse.ActivityDetailResponse;
// import sevenstar.marineleisure.activity.dto.reponse.ActivitySummaryResponse;
// import sevenstar.marineleisure.activity.dto.reponse.ActivityWeatherResponse;
// import sevenstar.marineleisure.activity.dto.reponse.activitiyDetailResponse.ActivityDetail;
// import sevenstar.marineleisure.activity.dto.reponse.activitiyDetailResponse.mapper.ActivityDetailMapper;
// import sevenstar.marineleisure.forecast.domain.Fishing;
// import sevenstar.marineleisure.forecast.domain.Mudflat;
// import sevenstar.marineleisure.forecast.domain.Scuba;
// import sevenstar.marineleisure.forecast.domain.Surfing;
// import sevenstar.marineleisure.forecast.repository.FishingRepository;
// import sevenstar.marineleisure.forecast.repository.MudflatRepository;
// import sevenstar.marineleisure.forecast.repository.ScubaRepository;
// import sevenstar.marineleisure.forecast.repository.SurfingRepository;
// import sevenstar.marineleisure.global.enums.ActivityCategory;
// import sevenstar.marineleisure.spot.domain.OutdoorSpot;
// import sevenstar.marineleisure.spot.repository.OutdoorSpotRepository;
//
// @Service
// @RequiredArgsConstructor
// public class ActivityService {
//
//     private final OutdoorSpotRepository outdoorSpotRepository;
//
//     private final FishingRepository fishingRepository;
//     private final MudflatRepository mudflatRepository;
//     private final ScubaRepository scubaRepository;
//     private final SurfingRepository surfingRepository;
//
//     @Transactional(readOnly = true)
//     public Map<String, ActivitySummaryResponse> getActivitySummary(BigDecimal latitude, BigDecimal longitude,
//         boolean global) {
//         if (global) {
//             return getGlobalActivitySummary();
//         } else {
//             return getLocalActivitySummary(latitude, longitude);
//         }
//     }
//
//     private Map<String, ActivitySummaryResponse> getLocalActivitySummary(BigDecimal latitude, BigDecimal longitude) {
//         Map<String, ActivitySummaryResponse> responses = new HashMap<>();
//
//         Fishing fishingBySpot = null;
//         Mudflat mudflatBySpot = null;
//         Surfing surfingBySpot = null;
//         Scuba scubaBySpot = null;
//
//         LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
//         LocalDateTime endOfDay = startOfDay.plusDays(1);
//
//         List<OutdoorSpot> outdoorSpotList = outdoorSpotRepository.findByCoordinates(latitude, longitude, 10);
//
//         while (fishingBySpot == null || mudflatBySpot == null || surfingBySpot == null || scubaBySpot == null) {
//
//             OutdoorSpot currentSpot;
//             Long currentSpotId;
//
//             try {
//                 currentSpot = outdoorSpotList.removeFirst();
//                 currentSpotId = currentSpot.getId();
//             } catch (Exception e) {
//                 break;
//             }
//
//             if (fishingBySpot == null) {
//                 Optional<Fishing> fishingResult = fishingRepository.findFirstBySpotIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
//                     currentSpotId, startOfDay, endOfDay);
//
//                 if (fishingResult.isPresent()) {
//                     fishingBySpot = fishingResult.get();
//                     responses.put("Fishing",
//                         new ActivitySummaryResponse(currentSpot.getName(), fishingResult.get().getTotalIndex()));
//                 }
//             }
//
//             if (mudflatBySpot == null) {
//                 Optional<Mudflat> mudflatResult = mudflatRepository.findFirstBySpotIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
//                     currentSpotId, startOfDay, endOfDay);
//
//                 if (mudflatResult.isPresent()) {
//                     mudflatBySpot = mudflatResult.get();
//                     responses.put("Mudflat",
//                         new ActivitySummaryResponse(currentSpot.getName(), mudflatResult.get().getTotalIndex()));
//                 }
//             }
//
//             if (surfingBySpot == null) {
//                 Optional<Surfing> surfingResult = surfingRepository.findFirstBySpotIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
//                     currentSpotId, startOfDay, endOfDay);
//
//                 if (surfingResult.isPresent()) {
//                     surfingBySpot = surfingResult.get();
//                     responses.put("Surfing",
//                         new ActivitySummaryResponse(currentSpot.getName(), surfingResult.get().getTotalIndex()));
//                 }
//             }
//
//             if (scubaBySpot == null) {
//                 Optional<Scuba> scubaResult = scubaRepository.findFirstBySpotIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
//                     currentSpotId, startOfDay, endOfDay);
//
//                 if (scubaResult.isPresent()) {
//                     scubaBySpot = scubaResult.get();
//                     responses.put("Scuba",
//                         new ActivitySummaryResponse(currentSpot.getName(), scubaResult.get().getTotalIndex()));
//                 }
//             }
//         }
//
//         return responses;
//     }
//
//     private Map<String, ActivitySummaryResponse> getGlobalActivitySummary() {
//         Map<String, ActivitySummaryResponse> responses = new HashMap<>();
//
//         LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
//         LocalDateTime endOfDay = startOfDay.plusDays(1);
//
//         Optional<Fishing> fishingResult = fishingRepository.findTopByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByTotalIndexDesc(
//             startOfDay, endOfDay);
//         Optional<Mudflat> mudflatResult = mudflatRepository.findTopByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByTotalIndexDesc(
//             startOfDay, endOfDay);
//         Optional<Surfing> surfingResult = surfingRepository.findTopByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByTotalIndexDesc(
//             startOfDay, endOfDay);
//         Optional<Scuba> scubaResult = scubaRepository.findTopByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByTotalIndexDesc(
//             startOfDay, endOfDay);
//
//         if (fishingResult.isPresent()) {
//             Fishing fishing = fishingResult.get();
//             OutdoorSpot spot = outdoorSpotRepository.findById(fishing.getSpotId()).get();
//             responses.put("Fishing", new ActivitySummaryResponse(spot.getName(), fishing.getTotalIndex()));
//         }
//
//         if (mudflatResult.isPresent()) {
//             Mudflat mudflat = mudflatResult.get();
//             OutdoorSpot spot = outdoorSpotRepository.findById(mudflat.getSpotId()).get();
//             responses.put("Mudflat", new ActivitySummaryResponse(spot.getName(), mudflat.getTotalIndex()));
//         }
//
//         if (scubaResult.isPresent()) {
//             Scuba scuba = scubaResult.get();
//             OutdoorSpot spot = outdoorSpotRepository.findById(scuba.getSpotId()).get();
//             responses.put("Scuba", new ActivitySummaryResponse(spot.getName(), scuba.getTotalIndex()));
//         }
//
//         if (surfingResult.isPresent()) {
//             Surfing surfing = surfingResult.get();
//             OutdoorSpot spot = outdoorSpotRepository.findById(surfing.getSpotId()).get();
//             responses.put("Surfing", new ActivitySummaryResponse(spot.getName(), surfing.getTotalIndex()));
//         }
//
//         return responses;
//     }
//
//     @Transactional(readOnly = true)
//     public ActivityDetailResponse getActivityDetail(ActivityCategory activity, BigDecimal latitude,
//         BigDecimal longitude) {
//
//         OutdoorSpot nearSpot = outdoorSpotRepository.findByCoordinates(latitude, longitude, 1).getFirst();
//
//         LocalDateTime today = LocalDate.now().plusDays(1).atStartOfDay();
//
//         ActivityDetail result;
//
//         switch (activity) {
//             case FISHING -> {
//                 Fishing resultSearch = fishingRepository.findBySpotIdAndCreatedAtBeforeOrderByCreatedAtDesc(
//                     nearSpot.getId(), today).get();
//                 result = ActivityDetailMapper.fromFishing(resultSearch);
//             }
//             case MUDFLAT -> {
//                 Mudflat resultSearch = mudflatRepository.findBySpotIdAndCreatedAtBeforeOrderByCreatedAtDesc(
//                     nearSpot.getId(), today).get();
//                 result = ActivityDetailMapper.fromMudflat(resultSearch);
//             }
//             case SURFING -> {
//                 Surfing resultSearch = surfingRepository.findBySpotIdAndCreatedAtBeforeOrderByCreatedAtDesc(
//                     nearSpot.getId(), today).get();
//                 result = ActivityDetailMapper.fromSurfing(resultSearch);
//             }
//             case SCUBA -> {
//                 Scuba resultSearch = scubaRepository.findBySpotIdAndCreatedAtBeforeOrderByCreatedAtDesc(
//                     nearSpot.getId(), today).get();
//                 result = ActivityDetailMapper.fromScuba(resultSearch);
//             }
//             default -> {
//                 throw new RuntimeException("WRONG_ACTIVITY");
//             }
//         }
//
//         return new ActivityDetailResponse(activity.toString(), nearSpot.getLocation(), result);
//     }
//
//     @Transactional(readOnly = true)
//     public ActivityWeatherResponse getWeatherBySpot(BigDecimal latitude, BigDecimal longitude) {
//         OutdoorSpot nearSpot = outdoorSpotRepository.findByCoordinates(latitude, longitude, 1).getFirst();
//
//         Fishing fishingSpot = fishingRepository.findBySpotIdOrderByCreatedAt(nearSpot.getId()).get();
//
//         if (fishingSpot != null) {
//             return new ActivityWeatherResponse(
//                 nearSpot.getName(),
//                 fishingSpot.getWindSpeedMax().toString(),
//                 fishingSpot.getWaveHeightMax().toString(),
//                 fishingSpot.getSeaTempMax().toString()
//             );
//         }
//
//         Surfing surfingSpot = surfingRepository.findBySpotIdOrderByCreatedAt(nearSpot.getId()).get();
//
//         if (surfingSpot != null) {
//             return new ActivityWeatherResponse(
//                 nearSpot.getName(),
//                 surfingSpot.getWindSpeed().toString(),
//                 surfingSpot.getWaveHeight().toString(),
//                 surfingSpot.getSeaTemp().toString()
//             );
//         } else {
//             throw new RuntimeException("Spot not found");
//         }
//     }
// }
