package gift.external.kakao;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gift.entity.member.Member;
import gift.entity.member.value.Role;
import gift.entity.product.Product;
import gift.entity.product.option.ProductOption;
import gift.entity.product.order.Order;
import gift.external.kakao.message.KakaoMessageClient;
import gift.fixture.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class KakaoMessageClientTest {

    private final String url = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    KakaoMessageClient kakaoMessageClient;

    private Order dummyOrder() {
        Product product = Product.of(1L, "상품A", 1000, "http://asdfimg.png", false);
        ProductOption option = ProductOption.of(product, "옵션1", 2);
        return Order.of(option, 2, "생일 축하해요");
    }

    private Member dummyMember() {
        Member member = MemberFixture.newRegisteredMember(
                1L, "user@example.com", "password", Role.USER);
        return member.withId(1L);
    }

    @Nested
    class SendToMeTest {

        @Test
        @DisplayName("정상적으로 메시지를 전송한다")
        void success() {
            when(restTemplate.postForEntity(eq(url), any(HttpEntity.class), eq(String.class)))
                    .thenReturn(ResponseEntity.ok("ok"));

            kakaoMessageClient.sendToMe("access-token", "{\"dummy\":true}");

            verify(restTemplate).postForEntity(eq(url), any(HttpEntity.class), eq(String.class));
        }

        @Test
        @DisplayName("401 Unauthorized 발생 시 RuntimeException 발생")
        void unauthorizedError() {
            HttpClientErrorException ex = HttpClientErrorException.create(
                    HttpStatus.UNAUTHORIZED,
                    "Unauthorized",
                    HttpHeaders.EMPTY,
                    "{\"msg\":\"invalid token\"}".getBytes(),
                    null
            );
            when(restTemplate.postForEntity(eq(url), any(HttpEntity.class), eq(String.class)))
                    .thenThrow(ex);

            assertThatThrownBy(() ->
                    kakaoMessageClient.sendToMe("invalid-token", "{}")
            )
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("카카오 메세지 전송 실패")
                    .hasCauseInstanceOf(HttpClientErrorException.class);
        }
    }
}
