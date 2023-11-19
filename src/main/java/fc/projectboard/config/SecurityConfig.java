package fc.projectboard.config;

import fc.projectboard.dto.UserAccountDto;
import fc.projectboard.dto.security.BoardPrincipal;
import fc.projectboard.service.UserAccountService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static fc.projectboard.config.OAuth2Config.OAuth2ManageKakao;
import static fc.projectboard.config.OAuth2Config.OAuth2ManageNaver;

@Configuration
public class SecurityConfig {

    private static final String[] requestPermitAllUrl = new String[]{
            "/",
            "/articles",
            "/articles/search-hashtag"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService, HandlerMappingIntrospector introspector) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers(
                                Stream.of(requestPermitAllUrl)
                                        .map(new MvcRequestMatcher.Builder(introspector)::pattern)
                                        .toArray(MvcRequestMatcher[]::new)
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults()) // 아무 동작도 안하고 싶을때 withDefaults()를 넣는다. (1번째 방법은 이걸 넣지 않고 .and()를 쓰는 방법, 2번째 방법은 람다식이나 이걸 넣고 and()를 쓰지 않음)
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .oauth2Login(oAuth -> oAuth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                )
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserAccountService userAccountService) {
        return username -> userAccountService
                .searchUser(username)
                .map(BoardPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다 - username: " + username));
    }

    // /user/me 꺼내는 과정. 자기 자신에 대한 rest-api 정보
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(UserAccountService userAccountService, PasswordEncoder passwordEncoder) {
        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return userRequest -> {
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            String registrationId = userRequest.getClientRegistration().getRegistrationId(); // application.yaml 의 registrationId. 즉, kakao, naver 등.
            Map<String, Object> attributes = oAuth2User.getAttributes();
            UserAccountDto userAccountDto;

            if(registrationId.equals("kakao")) {
                userAccountDto = OAuth2ManageKakao(attributes, passwordEncoder);
            }else if(registrationId.equals("naver")){
                userAccountDto = OAuth2ManageNaver(attributes, passwordEncoder);
            } else {
                userAccountDto = null;
            }

            // 디비에 이미 있다면 로그인. 없으면 회원가입
            return userAccountService.searchUser(Objects.requireNonNull(userAccountDto).userId())
                    .map(BoardPrincipal::from)
                    .orElseGet(()->
                            BoardPrincipal.from(
                                    userAccountService.saveUser(userAccountDto.userId(), userAccountDto.userPassword(), userAccountDto.email(), userAccountDto.nickname(), null)
                            )
                    );
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
