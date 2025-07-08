package sevenstar.marineleisure.forecast.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sevenstar.marineleisure.forecast.domain.FishingTarget;

public interface FishingTargetRepository extends JpaRepository<FishingTarget, Long> {
	Optional<FishingTarget> findByName(String name);
}