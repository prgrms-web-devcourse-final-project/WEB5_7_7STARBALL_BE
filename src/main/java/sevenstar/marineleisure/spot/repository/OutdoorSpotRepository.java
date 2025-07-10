package sevenstar.marineleisure.spot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sevenstar.marineleisure.spot.domain.OutdoorSpot;

public interface OutdoorSpotRepository extends JpaRepository<OutdoorSpot, Long> {
	Optional<OutdoorSpot> findByLocation(String location);

}
