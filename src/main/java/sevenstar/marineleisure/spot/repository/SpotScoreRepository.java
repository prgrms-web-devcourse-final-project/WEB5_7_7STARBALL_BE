package sevenstar.marineleisure.spot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sevenstar.marineleisure.spot.domain.SpotPreset;

public interface SpotScoreRepository extends JpaRepository<SpotPreset, Long> {
}