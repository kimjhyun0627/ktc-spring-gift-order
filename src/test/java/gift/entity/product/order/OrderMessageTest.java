package gift.entity.product.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gift.entity.product.order.value.OrderMessage;
import org.junit.jupiter.api.Test;

class OrderMessageTest {

    @Test
    void 메시지가_정상적으로_생성된다() {
        // given
        String value = "축하해요!";

        // when
        OrderMessage message = new OrderMessage(value);

        // then
        assertThat(message.message()).isEqualTo(value);
        assertThat(message.isBlank()).isFalse();
    }

    @Test
    void 메시지가_null이면_예외는_발생하지_않는다() {
        // when
        OrderMessage message = new OrderMessage(null);

        // then
        assertThat(message.message()).isNull();
        assertThat(message.isBlank()).isTrue();
    }

    @Test
    void 메시지가_500자를_초과하면_예외가_발생한다() {
        // given
        String longMessage = "a".repeat(501);

        // expect
        assertThatThrownBy(() -> new OrderMessage(longMessage))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("500자 이하여야");
    }

    @Test
    void isBlank_메시지가_null이면_true를_반환한다() {
        OrderMessage message = new OrderMessage(null);
        assertThat(message.isBlank()).isTrue();
    }

    @Test
    void isBlank_메시지가_공백이면_true를_반환한다() {
        OrderMessage message = new OrderMessage("   ");
        assertThat(message.isBlank()).isTrue();
    }

    @Test
    void isBlank_메시지가_내용이_있으면_false를_반환한다() {
        OrderMessage message = new OrderMessage("감사합니다");
        assertThat(message.isBlank()).isFalse();
    }
}
