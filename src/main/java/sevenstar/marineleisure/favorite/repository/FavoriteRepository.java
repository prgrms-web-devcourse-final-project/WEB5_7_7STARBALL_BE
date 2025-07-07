package sevenstar.marineleisure.favorite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sevenstar.marineleisure.favorite.domain.FavoriteSpot;

/**
 * MarineLeisure - FavoriteRepository
 * create date:    25. 7. 7.
 * last update:    25. 7. 7.
 * author:  gigol
 * purpose: 
 */
@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteSpot, Long> {
}
