package fc.projectboard.config;

import fc.projectboard.dto.UserAccountDto;
import fc.projectboard.dto.security.KakaoOAuth2Response;
import fc.projectboard.dto.security.NaverOAuth2Response;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.UUID;

public class OAuth2Config {
    private static String createUsername(String registrationId, String providerId){
        return registrationId + "_" + providerId;
    }
    private static String createDummyPassword(PasswordEncoder passwordEncoder){
        return passwordEncoder.encode("{bcrypt}" + UUID.randomUUID());
    }

    public static UserAccountDto OAuth2ManageKakao(Map<String, Object> attributes, PasswordEncoder passwordEncoder) {
        // 응답받은 json을 바로바로 뽑아 쓸수 있게끔 Response 객체로 변환하기.
        KakaoOAuth2Response kakaoResponse = KakaoOAuth2Response.from(attributes); // getAttributes 로 Map<String, Object> 꺼내고 객체변환.
        return UserAccountDto.of(
                createUsername("kakao", String.valueOf(kakaoResponse.id())),
                createDummyPassword(passwordEncoder),
                kakaoResponse.email(),
                kakaoResponse.nickname(),
                null
        );
    }

    public static UserAccountDto OAuth2ManageNaver(Map<String, Object> attributes, PasswordEncoder passwordEncoder) {
        NaverOAuth2Response naverResponse = NaverOAuth2Response.from(attributes);
        return UserAccountDto.of(
                createUsername("naver", naverResponse.id()),
                createDummyPassword(passwordEncoder),
                naverResponse.email(),
                naverResponse.nickname(),
                null
        );
    }
}
