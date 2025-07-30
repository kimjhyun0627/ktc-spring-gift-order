package gift.entity.product.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gift.entity.product.option.ProductOption;
import gift.entity.product.order.value.OrderMessage;
import gift.entity.product.order.value.OrderQuantity;
import gift.exception.custom.InvalidOptionException;
import gift.fixture.ProductOptionFixture;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void 주문_정상_생성() {
        // given
        ProductOption option = ProductOptionFixture.visible(1L, "색상 빨강", 10);
        int quantityValue = 3;
        String messageValue = "생일 축하해!";

        // when
        Order order = Order.of(option, quantityValue, messageValue);

        // then
        assertThat(order.getOption()).isEqualTo(option);

        OrderQuantity quantity = order.getQuantity();
        assertThat(quantity.amount()).isEqualTo(quantityValue);

        OrderMessage message = order.getMessage();
        assertThat(message.message()).isEqualTo(messageValue);

        LocalDateTime createdAt = order.getCreatedAt();
        assertThat(createdAt).isNotNull();
        assertThat(createdAt).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void 주문_수량이_0이면_예외가_발생한다() {
        // given
        ProductOption option = ProductOptionFixture.visible();
        int invalidQuantity = 0;
        String message = "선물용이에요";

        // expect
        assertThatThrownBy(() -> Order.of(option, invalidQuantity, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문 수량은 1개 이상이어야 합니다.");
    }

    @Test
    void 주문_옵션이_null이면_예외가_발생한다() {
        // given
        int quantity = 2;
        String message = "잘 부탁드립니다";

        // expect
        assertThatThrownBy(() -> Order.of(null, quantity, message))
                .isInstanceOf(InvalidOptionException.class)
                .hasMessageContaining("option");
    }
}
