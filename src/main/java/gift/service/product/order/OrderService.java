package gift.service.product.order;

import gift.dto.product.order.OrderRequest;
import gift.dto.product.order.OrderResponse;

public interface OrderService {

    OrderResponse placeOrder(Long memberId, OrderRequest request);
}
