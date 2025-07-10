package sevenstar.marineleisure.favorite.mapper;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.favorite.domain.FavoriteSpot;
import sevenstar.marineleisure.favorite.dto.response.FavoritePatchDto;

@Component
@RequiredArgsConstructor
public class FavoriteMapper {

	public FavoritePatchDto toPatchDto(FavoriteSpot fav) {
		return FavoritePatchDto.builder()
			.favoriteId(fav.getId())
			.notification(fav.getNotification())
			.build();
	}
}
