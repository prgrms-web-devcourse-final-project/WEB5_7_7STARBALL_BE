package sevenstar.marineleisure.alert.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sevenstar.marineleisure.alert.domain.JellyfishSpecies;

@Repository
public interface JellyfishSpeciesRepository extends JpaRepository<JellyfishSpecies, Long> {
	Optional<JellyfishSpecies> findByName(String name);

}
