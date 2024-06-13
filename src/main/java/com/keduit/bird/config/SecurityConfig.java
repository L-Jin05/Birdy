package com.keduit.bird.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        System.out.println("-------------- SecurityFilterChain-------------");

        http.formLogin()
                .loginPage("/members/login")
                .defaultSuccessUrl("/")
//      default 이동을 메인으로 설정
                .usernameParameter("memberEmail")
//      loadUserByUsername(String email)로 실행 / email을 불러와서
                .failureUrl("/members/login/error")
//      불일치로 오류나면 에러 내보내기, 아니면 default인 메인으로 이동
                .and()
//      그리고 ---
                .logout()
//      로그아웃을 시도해서
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
//      AntPathRequestMatcher = 디폴트 설정(logout 페이지를 따로 만들지 않아 아래 "/"를 따라 메인으로 돌아간다)
                .logoutSuccessUrl("/");
//      성공하면 메인으로

        //카카오추가(login)
        http.oauth2Login().loginPage("/members/login")
                .successHandler(autenticationSuccessHandler());

        http.authorizeRequests()
                // 특정 경로에 대한 접근 권한을 설정
                .mvcMatchers("/", "/members/**", "/images/**", "/intro/**", "favicon.ico", "error", "/member/**")
                // 접근 가능한 경로를 지정
                .permitAll()
                // 앞서 작성한 경로는 누구나 접근 가능하도록 All 설정
                .mvcMatchers("/bird/**", "/member/**", "/myPage/**", "/comment/**", "/board/**", "/intro/**")
                .hasAnyRole("MEMBER","ADMIN")
                .mvcMatchers("/admin/**")
                // "/admin/**" 경로에 대한 권한 설정
                .hasRole("ADMIN")
                // "ADMIN" 권한을 가진 사용자의 접근일 경우 가능하며,
                .anyRequest()
                // 모든 요청에 대한 인증 설정 = 모든 접근이 가능
                .authenticated()
                //이 메서드는 주로 보안 설정에서 모든 요청에 대한 마지막 규칙으로 사용됩니다.
                // 다른 규칙들이 적용되지 않은 요청에 대해서는 이 규칙이 적용되어 인증되지 않은 사용자는 접근을 거부합니다.
                .and()
                .csrf()
//                .disable();
                .ignoringAntMatchers("/members/**", "/myPage/memberStop", "/myPage/update","/board/**", "/comment/**","/admin/**");
        //"/mrmbers/**"경로에 대해서만 csrf 토큰 보호 off시킴.
//                .disable();
        //403 오류....짜증...보안 꺼버리기 <- 최후의 수단...

// 인증되지 않은 요청에 대한 처리 설정
        http.exceptionHandling()
                // 커스텀 인증 진입점을 설정 (CustomAuthenticationEntrypoint 클래스 사용)
                .authenticationEntryPoint(new CustomAuthenticationEntrypoint());


// 설정된 보안 구성 반환합니다.
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        // 정적 리소스에 대한 보안 설정을 무시하도록 지정
        return (web)-> web.ignoring()
                //보안 설정을 무시하도록 지정하는 함수
                .requestMatchers(PathRequest.toStaticResources()
                        // Spring Boot가 제공하는 일반적인 정적 리소스 위치에 대한 요청을 무시하도록 지정
                        .atCommonLocations());
        // 이렇게 함으로써 Spring Boot가 자동으로 제공하는 정적 리소스에 대한 요청은
        // Spring Security에 의해 처리되지 않고, 보안 검사를 통과하지 않습니다.
    }

    @Bean
    public AuthenticationSuccessHandler autenticationSuccessHandler(){
        return new CustomSocialLoginSeccessHandler(passwordEncoder());
    }
}