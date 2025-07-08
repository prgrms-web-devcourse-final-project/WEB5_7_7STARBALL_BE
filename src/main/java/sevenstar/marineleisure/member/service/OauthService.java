package sevenstar.marineleisure.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.member.repository.MemberRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthService {

    private final MemberRepository memberRepository;
    private final WebClient webClient;

    @Transactional
    public Map<String, Object> processKakaoUser(String accessToken) {
        // 1. access token으로 사용자 정보 요청
        Map<String, Object> memberAttributes = getUserInfo(accessToken);
        // 2. 사용자 정보로 회원가입 or 로그인 처리
        Member member = saveOrUpdateKakaoUser(memberAttributes);
        // 3. 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("id", member != null ? member.getId() : null);
        response.put("email", member != null ? member.getEmail() : null);
        response.put("nickname", member != null ? member.getNickname() : null);
        return response;
    }


    /**
     * 카카오 API로 사용자 정보 요청
     *
     * @param accessToken
     * @return
     */
    private Map<String, Object> getUserInfo(String accessToken) {
        return webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
    }

    /**
     * 카카오 사용자 정보로 회원가입 or 로그인 처리
     *
     * @param memberAttributes
     * @return
     */
    private Member saveOrUpdateKakaoUser(Map<String, Object> memberAttributes) {
        Long id = (Long) memberAttributes.get("id");
        Map<String, Object> kakaoAccount = (Map<String, Object>) memberAttributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");

        // 좌표 설정을 어떻게 하는가? update 시에 해줘야 할듯 한데.
        Member member = memberRepository.findByProviderAndProviderId("kakao", String.valueOf(id))
                .map(e -> e.update(nickname))
                .orElse(Member.builder()
                        .email(email)
                        .nickname(nickname)
                        .provider("kakao")
                        .providerId(String.valueOf(id))
                        .latitude(BigDecimal.valueOf(0))
                        .longitude(BigDecimal.valueOf(0))
                        .build()
                );

        return memberRepository.save(member);
    }


    public Member findUserById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found for id: " + id + " or email: " + id + "@kakao.com"));
    }
}
