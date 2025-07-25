package gift.external.kakao;

import gift.config.KakaoConfig;
import gift.exception.custom.InvalidAuthExeption;
import gift.external.kakao.dto.KakaoTokenResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoTokenClient {

    private final RestTemplate restTemplate;
    private final KakaoConfig kakaoConfig;

    private final String tokenUrl = "https://kauth.kakao.com/oauth/token";
    private final String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

    public KakaoTokenClient(RestTemplate restTemplate, KakaoConfig kakaoConfig) {
        this.restTemplate = restTemplate;
        this.kakaoConfig = kakaoConfig;
    }

    public String getAccessToken(String authorizationCode) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
            body.add("client_id", kakaoConfig.getClientId());
            body.add("redirect_uri", kakaoConfig.getRedirectUri());
            body.add("code", authorizationCode);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(
                    tokenUrl, request, KakaoTokenResponse.class
            );

            KakaoTokenResponse token = response.getBody();

            if (!response.getStatusCode().is2xxSuccessful() || token == null
                    || token.accessToken() == null || token.accessToken().isBlank()) {
                throw new InvalidAuthExeption("카카오로부터 유효한 액세스 토큰을 받지 못했습니다.");
            }

            return token.accessToken();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String body = ex.getResponseBodyAsString();
            throw new InvalidAuthExeption("카카오 토큰 요청 실패: " + body);
        } catch (Exception e) {
            throw new InvalidAuthExeption("카카오 토큰 요청 중 알 수 없는 오류 발생");
        }
    }

    public KakaoUserInfo getUserInfo(String accessToken) {
        try {
            HttpEntity<Void> request = new HttpEntity<>(createHeaders(accessToken));

            ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                    userInfoUrl,
                    HttpMethod.GET,
                    request,
                    KakaoUserInfo.class
            );

            KakaoUserInfo body = response.getBody();
            if (body == null || body.id() == null) {
                throw new InvalidAuthExeption("카카오 사용자 정보 응답이 유효하지 않습니다.");
            }

            return body;

        } catch (RestClientResponseException ex) {
            throw new InvalidAuthExeption("카카오 사용자 정보 요청 실패: " + ex.getResponseBodyAsString());
        } catch (Exception e) {
            throw new InvalidAuthExeption("카카오 사용자 정보 요청 중 오류 발생");
        }
    }
    
    private HttpHeaders createHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        return headers;
    }

}

