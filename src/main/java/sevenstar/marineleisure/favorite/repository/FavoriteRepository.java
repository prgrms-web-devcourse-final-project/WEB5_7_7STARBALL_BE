package sevenstar.marineleisure.favorite.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sevenstar.marineleisure.favorite.domain.FavoriteSpot;
import sevenstar.marineleisure.favorite.dto.vo.FavoriteItemVO;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteSpot, Long> {
	void deleteFavoriteSpotById(Long id);

	@Query("""
		SELECT new sevenstar.marineleisure.favorite.dto.vo.FavoriteItemVO(
			os.id,
		    fs.id,
		    os.name,
		    os.category,
		    os.location,
		    fs.notification
		)
		FROM FavoriteSpot fs
		JOIN OutdoorSpot os ON fs.spotId = os.id
		WHERE fs.memberId = :memberId
		AND (:cursorId IS NULL OR fs.id > :cursorId)
		ORDER BY fs.id ASC
		""")
	List<FavoriteItemVO> findFavoritesByMemberIdAndCursorId(
		@Param("memberId") Long memberId,
		@Param("cursorId") Long cursorId,
		Pageable pageable
	);
	boolean existsByMemberIdAndSpotId(Long memberId, Long spotId);
}
