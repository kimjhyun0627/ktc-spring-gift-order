package gift.external.kakao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import gift.config.KakaoConfig;
import gift.exception.custom.InvalidAuthExeption;
import gift.external.kakao.dto.KakaoTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class KakaoTokenClientTest {

    @Mock
    KakaoConfig kakaoConfig;

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    KakaoTokenClient kakaoTokenClient;

    @Nested
    class GetAccessTokenTest {

        @Test
        @DisplayName("정상적인 access_token 반환")
        void success() {
            KakaoTokenResponse mockResponse = new KakaoTokenResponse("access-token", "bearer",
                    3600);
            ResponseEntity<KakaoTokenResponse> entity = new ResponseEntity<>(mockResponse,
                    HttpStatus.OK);

            when(restTemplate.postForEntity(anyString(), any(), eq(KakaoTokenResponse.class)))
                    .thenReturn(entity);

            String token = kakaoTokenClient.getAccessToken("auth-code");

            assertThat(token).isEqualTo("access-token");
        }

        @Test
        @DisplayName("401 Unauthorized 발생 시 InvalidAuthExeption 발생")
        void unauthorizedError() {
            HttpClientErrorException ex = HttpClientErrorException.create(
                    HttpStatus.UNAUTHORIZED, "Unauthorized", HttpHeaders.EMPTY,
                    "invalid_grant".getBytes(), null
            );

            when(restTemplate.postForEntity(anyString(), any(), eq(KakaoTokenResponse.class)))
                    .thenThrow(ex);

            assertThatThrownBy(() -> kakaoTokenClient.getAccessToken("bad-code"))
                    .isInstanceOf(InvalidAuthExeption.class)
                    .hasMessageContaining("카카오 토큰 요청 실패");
        }

        @Test
        @DisplayName("500 Internal Server Error 발생 시 InvalidAuthExeption 발생")
        void serverError() {
            HttpServerErrorException ex = HttpServerErrorException.create(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Error", HttpHeaders.EMPTY,
                    "internal error".getBytes(), null
            );

            when(restTemplate.postForEntity(anyString(), any(), eq(KakaoTokenResponse.class)))
                    .thenThrow(ex);

            assertThatThrownBy(() -> kakaoTokenClient.getAccessToken("code"))
                    .isInstanceOf(InvalidAuthExeption.class);
        }

        @Test
        @DisplayName("access_token이 null일 경우 예외")
        void nullAccessToken() {
            KakaoTokenResponse response = new KakaoTokenResponse(null, "bearer", 3600);
            ResponseEntity<KakaoTokenResponse> entity = new ResponseEntity<>(response,
                    HttpStatus.OK);

            when(restTemplate.postForEntity(anyString(), any(), eq(KakaoTokenResponse.class)))
                    .thenReturn(entity);

            assertThatThrownBy(() -> kakaoTokenClient.getAccessToken("code"))
                    .isInstanceOf(InvalidAuthExeption.class)
                    .hasMessageContaining("카카오 토큰 요청 중 알 수 없는 오류 발생");
        }
    }

    @Nested
    class GetUserInfoTest {

        @Test
        @DisplayName("정상적으로 사용자 정보를 받아온다")
        void getUserInfoSuccess() {
            KakaoUserInfo.KakaoAccount account = new KakaoUserInfo.KakaoAccount("abc@example.com");
            KakaoUserInfo mockInfo = new KakaoUserInfo(12345L, account);
            ResponseEntity<KakaoUserInfo> entity = new ResponseEntity<>(mockInfo, HttpStatus.OK);

            when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
                    eq(KakaoUserInfo.class)))
                    .thenReturn(entity);

            KakaoUserInfo userInfo = kakaoTokenClient.getUserInfo("access-token");

            assertThat(userInfo.id()).isEqualTo(12345L);
            assertThat(userInfo.kakaoAccount().email()).isEqualTo("abc@example.com");
        }

        @Test
        @DisplayName("응답 body가 null일 경우 예외 발생")
        void nullBody() {
            ResponseEntity<KakaoUserInfo> entity = new ResponseEntity<>(null, HttpStatus.OK);

            when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
                    eq(KakaoUserInfo.class)))
                    .thenReturn(entity);

            assertThatThrownBy(() -> kakaoTokenClient.getUserInfo("access-token"))
                    .isInstanceOf(InvalidAuthExeption.class)
                    .hasMessageContaining("카카오 사용자 정보 요청 중 오류 발생");
        }

        @Test
        @DisplayName("id가 null이면 예외 발생")
        void nullId() {
            KakaoUserInfo info = new KakaoUserInfo(null, new KakaoUserInfo.KakaoAccount("a@b.com"));
            ResponseEntity<KakaoUserInfo> entity = new ResponseEntity<>(info, HttpStatus.OK);

            when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
                    eq(KakaoUserInfo.class)))
                    .thenReturn(entity);

            assertThatThrownBy(() -> kakaoTokenClient.getUserInfo("access-token"))
                    .isInstanceOf(InvalidAuthExeption.class);
        }

        @Test
        @DisplayName("403 Forbidden 발생 시 예외 처리")
        void forbiddenError() {
            HttpClientErrorException ex = HttpClientErrorException.create(
                    HttpStatus.FORBIDDEN, "Forbidden", HttpHeaders.EMPTY,
                    "{\"msg\":\"scope denied\"}".getBytes(), null
            );

            when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
                    eq(KakaoUserInfo.class)))
                    .thenThrow(ex);

            assertThatThrownBy(() -> kakaoTokenClient.getUserInfo("access-token"))
                    .isInstanceOf(InvalidAuthExeption.class)
                    .hasMessageContaining("요청 실패");
        }
    }
}
