package sevenstar.marineleisure.forecast.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sevenstar.marineleisure.forecast.domain.Fishing;

public interface FishingRepository extends JpaRepository<Fishing, Long> {
}