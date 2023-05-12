package com.codestates.sebmainproject009.auth.handler;

import com.codestates.sebmainproject009.auth.jwt.JwtTokenizer;
import com.codestates.sebmainproject009.auth.utils.CustomAuthorityUtils;
import com.codestates.sebmainproject009.user.repository.UserRepository;
import com.codestates.sebmainproject009.user.service.UserService;
import com.codestates.sebmainproject009.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OAuth2UserSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;
    private final UserService userService;

    // 객체를 DI 해주기
    public OAuth2UserSuccessHandler(JwtTokenizer jwtTokenizer, CustomAuthorityUtils authorityUtils, UserService userService) {
        this.jwtTokenizer = jwtTokenizer;
        this.authorityUtils = authorityUtils;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        var oAuth2User = (OAuth2User)authentication.getPrincipal();


        StringBuffer URL = request.getRequestURL();
        String provider = URL.substring(URL.lastIndexOf("/") + 1);

        Map<String, Object> responseMap;

        switch (provider){
            case "kakao":
                responseMap = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
                break;
            case "naver":
                responseMap = (Map<String, Object>) oAuth2User.getAttributes().get("response");
                break;
            default: // google
                responseMap =  (Map<String, Object>) oAuth2User.getAttributes();
                break;
        }



        responseMap.forEach((key, value) -> System.out.println(key + " = " + value));




        String email = String.valueOf(oAuth2User.getAttributes().get("email"));
        String displayName = String.valueOf(oAuth2User.getAttributes().get("name"));

        List<String> authorities = authorityUtils.createRoles(email);


        // 유저없으면 email, displayName 저장
        if(!userService.verifyUserByEmail(email)) {
            saveUser(email, displayName);
        }
        redirect(request, response, email, authorities); // Access Token 과 Refresh Token 을 생성해서 Frontend 애플리케이션에 전달
    }

    private void saveUser(String email, String displayName) throws IOException{
        User user = new User(email,displayName);
        user.setPassword("");
        userService.createUser(user);
    }

    private void redirect(HttpServletRequest request, HttpServletResponse response, String username, List<String> authorities) throws IOException {
        String accessToken = delegateAccessToken(username, authorities);
        String refreshToken = delegateRefreshToken(username);

        String uri = createURI(accessToken, refreshToken).toString();
        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private URI createURI(String accessToken, String refreshToken) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("access_token", accessToken);
        queryParams.add("refresh_token", refreshToken);

        return UriComponentsBuilder
                .newInstance()
                //.scheme("http")
                .scheme("https")
                .host("dowajoyak.shop")
                //.host("localhost")
               // .port(80) // 클라이언트 쪽
                .port(443)
                //.path("/receive-token.html")
                .queryParams(queryParams)
                .build()
                .toUri();
    }

    private String delegateAccessToken(String username, List<String> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("roles", authorities);

        User user = userService.findUser(username);
        claims.put("userId",user.getUserId());

        String subject = username;
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());

        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        String accessToken = jwtTokenizer.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);

        return accessToken;
    }

    private String delegateRefreshToken(String username) {
        String subject = username;
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getRefreshTokenExpirationMinutes());
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        String refreshToken = jwtTokenizer.generateRefreshToken(subject, expiration, base64EncodedSecretKey);

        return refreshToken;
    }

}
