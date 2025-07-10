package sevenstar.marineleisure.activity.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.activity.dto.reponse.ActivitySummaryResponse;
import sevenstar.marineleisure.forecast.domain.Fishing;
import sevenstar.marineleisure.forecast.domain.Mudflat;
import sevenstar.marineleisure.forecast.domain.Scuba;
import sevenstar.marineleisure.forecast.domain.Surfing;
import sevenstar.marineleisure.forecast.repository.FishingRepository;
import sevenstar.marineleisure.forecast.repository.MudflatRepository;
import sevenstar.marineleisure.forecast.repository.ScubaRepository;
import sevenstar.marineleisure.forecast.repository.SurfingRepository;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final FishingRepository fishingRepository;
    private final MudflatRepository mudflatRepository;
    private final ScubaRepository scubaRepository;
    private final SurfingRepository surfingRepository;


    public Map<String, ActivitySummaryResponse> getActivitySummary(float latitude, float longitude, boolean global) {
        Map<String, ActivitySummaryResponse> responses = new HashMap<>();

        // List<Fishing> fishingList;
        // List<Mudflat> mudflatList;
        // List<Scuba> scubaList;
        // List<Surfing> surfingList;

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime nextDayStartOfDay = LocalDate.now().plusDays(1).atStartOfDay();

        System.out.println("startOfDay: " + startOfDay);
        System.out.println("nextDayStartOfDay: " + nextDayStartOfDay);


        if (global) {
            List<Fishing> fishingDailyList = fishingRepository.findByDay(startOfDay, nextDayStartOfDay);
            List<Mudflat> mudflatDailyList = mudflatRepository.findByDay(startOfDay, nextDayStartOfDay);
            List<Scuba>     scubaDailyList = scubaRepository.findByDay(startOfDay, nextDayStartOfDay);
            List<Surfing> surfingDailyList = surfingRepository.findByDay(startOfDay, nextDayStartOfDay);

            // fishingList = fishingRepository.findByDay(startOfDay, nextDayStartOfDay);
        }
        // else {
        //
        //     // fishingList = fishingRepository.findByRadius();
        //     // mudflatList = mudflatRepository.findByRadius();
        //     // scubaList = scubaRepository.findByRadius();
        //     // surfingList = surfingRepository.findByRadius();
        // }

        return responses;
    }

    public void getForecastDetail() {

    }
}
