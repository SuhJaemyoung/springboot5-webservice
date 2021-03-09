package com.jaemyoung.book.springboot.config.auth;

import com.jaemyoung.book.springboot.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()   // h2--console 화면을 사용하기 위해 해당 옵션들을 disable 함.
            .headers().frameOptions().disable() // h2--console 화면을 사용하기 위해 해당 옵션들을 disable 함.
            .and()
                .authorizeRequests()        // URL 별 권한 관리를 설정하는 옵션의 시작점.
                                            // authorizeRequests 가 선언되어야만 antMatchers 옵션을 사용할 수 있음.
                .antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**").permitAll()
                .antMatchers("/api/v1/**").hasRole(Role.USER.name())
                // 권한 관리 대상을 설정하는 옵션
                .anyRequest().authenticated()       // 설정된 값들 이외 나머지 URL 들은 모두 인증된 사용자들에게만 허용함. (인증된 사용자 => 로그인한 사용자들)
            .and()
                .logout()
                    .logoutSuccessUrl("/")      // 로그아웃 시 이동 경로 설정
            .and()
                .oauth2Login()                  // 로그인 기능에 대한 여러 설정의 진입점.
                    .userInfoEndpoint()         // 로그인 성공 시 사용자 정보를 가져올 때의 설정 담당.
                        .userService(customOAuth2UserService);      // 소셜 로그인 성공시 후속 조치를 진행할 UserService 인터페이스의 구현체를 등록함.
                                                                    // 소셜 서비스들에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능을 명시할 수 있다.
    }
}
