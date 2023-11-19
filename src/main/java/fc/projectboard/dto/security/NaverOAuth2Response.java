package fc.projectboard.dto.security;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@SuppressWarnings("unchecked")
public record NaverOAuth2Response(
        String id,
        String nickname,
        String name,
        String email
) {
    public static NaverOAuth2Response from(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return new NaverOAuth2Response(
                String.valueOf(response.get("id")),
                String.valueOf(response.get("nickname")),
                String.valueOf(response.get("name")),
                String.valueOf(response.get("email"))
        );
    }
}
