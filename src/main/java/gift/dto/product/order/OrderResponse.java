package gift.dto.product.order;

import gift.entity.product.order.Order;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        Long optionId,
        int quantity,
        LocalDateTime orderDateTime,
        String message
) {

    public static OrderResponse of(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOption().getId(),
                order.getQuantity().amount(),
                order.getCreatedAt(),
                order.getMessage().message()
        );
    }
}
