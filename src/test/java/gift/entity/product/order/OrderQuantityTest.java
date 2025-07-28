package gift.entity.product.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gift.entity.product.order.value.OrderQuantity;
import org.junit.jupiter.api.Test;

class OrderQuantityTest {

    @Test
    void 수량이_1개_이상이면_정상_생성된다() {
        // given
        int validAmount = 3;

        // when
        OrderQuantity quantity = new OrderQuantity(validAmount);

        // then
        assertThat(quantity.amount()).isEqualTo(validAmount);
    }

    @Test
    void 수량이_1이면_정상_생성된다() {
        OrderQuantity quantity = new OrderQuantity(1);
        assertThat(quantity.amount()).isEqualTo(1);
    }

    @Test
    void 수량이_0이면_예외가_발생한다() {
        assertThatThrownBy(() -> new OrderQuantity(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1개 이상");
    }

    @Test
    void 수량이_음수이면_예외가_발생한다() {
        assertThatThrownBy(() -> new OrderQuantity(-5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1개 이상");
    }
}
