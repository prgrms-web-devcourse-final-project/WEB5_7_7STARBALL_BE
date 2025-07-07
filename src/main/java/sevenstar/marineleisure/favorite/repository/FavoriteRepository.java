package sevenstar.marineleisure.favorite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sevenstar.marineleisure.favorite.domain.FavoriteSpot;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteSpot, Long> {
}
