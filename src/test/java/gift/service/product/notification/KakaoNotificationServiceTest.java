package gift.service.product.notification;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.entity.member.Member;
import gift.entity.member.value.Role;
import gift.entity.product.Product;
import gift.entity.product.option.ProductOption;
import gift.entity.product.order.Order;
import gift.external.kakao.message.KakaoMessageClient;
import gift.fixture.MemberFixture;
import gift.service.product.order.notification.KakaoNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class KakaoNotificationServiceTest {

    @Captor
    ArgumentCaptor<String> templateCaptor;
    @Mock
    private KakaoMessageClient kakaoMessageClient;
    @InjectMocks
    private KakaoNotificationService service;
    private Order dummyOrder;
    private Member dummyMember;

    @BeforeEach
    void setUp() {
        Product product = Product.of(1L, "상품A", 2000, "http://img.png", false);
        ProductOption option = ProductOption.of(product, "옵션B", 3);
        dummyOrder = Order.of(option, 5, "감사합니다");

        dummyMember = MemberFixture.newRegisteredMember(
                        2L, "test@domain.com", "pass", Role.USER)
                .withId(2L);
    }

    @Test
    @DisplayName("notifyOrderCompleted 호출 시 KakaoMessageClient에 올바른 파라미터 전달")
    void notifySuccess() {
        service.notifyOrderCompleted(dummyOrder, dummyMember);

        verify(kakaoMessageClient).sendToMe(
                eq(dummyMember.getAccessToken()),
                templateCaptor.capture()
        );

        String json = templateCaptor.getValue();

        assert json.contains("상품: " + dummyOrder.getOption().getName().name());
        assert json.contains("수량: " + dummyOrder.getQuantity().amount());
        assert json.contains(dummyMember.getEmail().email());
    }

    @Test
    @DisplayName("메시지 생성 실패 시 RuntimeException 발생")
    void templateError() throws JsonProcessingException {
        ObjectMapper mockMapper = org.mockito.Mockito.mock(ObjectMapper.class);
        when(mockMapper.writeValueAsString(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new JsonProcessingException("fail") {
                });
        ReflectionTestUtils.setField(service, "objectMapper", mockMapper);

        assertThatThrownBy(() ->
                service.notifyOrderCompleted(dummyOrder, dummyMember)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("메시지 생성 실패");
    }
}
