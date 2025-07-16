package sevenstar.marineleisure.spot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sevenstar.marineleisure.spot.domain.SpotScore;

public interface SpotScoreRepository extends JpaRepository<SpotScore, Long> {
}