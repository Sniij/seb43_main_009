package com.codestates.sebmainproject009.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {//인증 방식과 웹페이지 접근 권한 설정

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{// http 보안설정
        http
                .csrf().disable()
                .formLogin()
                .loginPage("/auths/login-form")
                .loginProcessingUrl("/process_login")
                .failureUrl("/auths/login-form?error")
                .and()
                .authorizeHttpRequests()
                .anyRequest()
                .permitAll();
        return http.build();

    }

    @Bean
    public UserDetailsManager userDetailsManager(){//인터페이스
        UserDetails userDetails =
                User.withDefaultPasswordEncoder()//패스워드 암호화
                        .username("kevin@gmail.com")//고유 사용자를 식별할 수 있는 아이디
                        .password("1111")//1111를 암호화
                        .roles("USER")//역할 지정
                        .build();

        return new InMemoryUserDetailsManager(userDetails);
    }
}