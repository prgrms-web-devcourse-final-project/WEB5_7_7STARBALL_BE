package sevenstar.marineleisure.forecast.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sevenstar.marineleisure.forecast.domain.Mudflat;

public interface MudflatRepository extends JpaRepository<Mudflat, Long> {
}