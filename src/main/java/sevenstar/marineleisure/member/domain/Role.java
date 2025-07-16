package sevenstar.marineleisure.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    GUEST("ROLE_GUEST", "일반 유저"),
    OWNER("ROlE_OWNER", "모임 생성자");

    private final String key;
    private final String value;
}
