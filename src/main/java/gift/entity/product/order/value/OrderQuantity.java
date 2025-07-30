package gift.entity.product.order.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record OrderQuantity(
        @Column(name = "amount", nullable = false)
        int amount
) {

    public OrderQuantity {
        if (amount <= 0) {
            throw new IllegalArgumentException("주문 수량은 1개 이상이어야 합니다.");
        }
    }
}
