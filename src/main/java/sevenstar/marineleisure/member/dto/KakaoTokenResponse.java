package sevenstar.marineleisure.member.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

/**
 *
 * @param accessToken
 * @param tokenType
 * @param refreshToken
 * @param expiresIn
 * @param scope
 * @param refreshTokenExpiresIn
 */
@Builder
public record KakaoTokenResponse(
	@JsonProperty("access_token") String accessToken,
	@JsonProperty("token_type") String tokenType,
	@JsonProperty("refresh_token") String refreshToken,
	@JsonProperty("expires_in") Long expiresIn,
	@JsonProperty("scope") String scope,
	@JsonProperty("refresh_token_expires_in") Long refreshTokenExpiresIn
) {
	@JsonCreator
	public KakaoTokenResponse {
	}
}
