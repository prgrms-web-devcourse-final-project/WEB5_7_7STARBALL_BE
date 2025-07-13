package sevenstar.marineleisure.member.dto;

/**
 * 브라우저가 받은 인증 코드를 서버로 전달하기 위한 DTO
 *
 * @param code  : 프론트엔드에서 받을 인증 코드
 * @param state : 프론트엔드에서 받을 상태
 * @param encryptedState : 암호화된 상태 값 (stateless 인증을 위해 사용)
 */
public record AuthCodeRequest(
	String code,
	String state,
	String encryptedState
) {
}
