package sevenstar.marineleisure.favorite.dto.response;

import lombok.Builder;

@Builder
public record FavoritePatchDto(Long favoriteId, boolean notification) {
}
