package sevenstar.marineleisure.member.dto;

import lombok.Getter;

import java.util.Map;

@Getter
public class OauthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String nickname;
    private String email;
    private String provider;
    private String providerId;
}