package sevenstar.marineleisure.member.dto;

/**
 * 브라우저가 받은 인증 코드를 서버로 전달하기 위한 DTO
 *
 * @param code  : 프론트엔드에서 받을 인증 코드 (성공 시)
 * @param state : 프론트엔드에서 받을 상태
 * @param encryptedState : 암호화된 상태 값 (stateless 인증을 위해 사용)
 * @param error : 인증 실패 시 반환되는 에러 코드
 * @param errorDescription : 인증 실패 시 반환되는 에러 메시지
 */
public record AuthCodeRequest(
	String code,
	String state,
	String encryptedState,
	String error,
	String errorDescription
) {
}
