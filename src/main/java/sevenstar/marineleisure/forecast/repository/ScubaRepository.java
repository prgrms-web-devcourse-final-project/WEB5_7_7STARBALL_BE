package sevenstar.marineleisure.forecast.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sevenstar.marineleisure.forecast.domain.Scuba;

public interface ScubaRepository extends JpaRepository<Scuba, Long> {
}