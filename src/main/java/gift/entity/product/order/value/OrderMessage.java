package gift.entity.product.order.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record OrderMessage(
        @Column(name = "message", length = 500)
        String message
) {

    public OrderMessage {
        if (message != null && message.length() > 500) {
            throw new IllegalArgumentException("메시지는 500자 이하여야 합니다.");
        }
    }

    public boolean isBlank() {
        return message == null || message.isBlank();
    }
}
