package sevenstar.marineleisure.activity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sevenstar.marineleisure.activity.domain.Weather;

public interface WeatherRepository extends JpaRepository<Weather,Long> {
    Optional<Weather> findByOutdoorSpotId(Long outdoorSpotId);
}
