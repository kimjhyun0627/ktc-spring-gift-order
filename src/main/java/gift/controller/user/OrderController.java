package gift.controller.user;

import gift.dto.product.order.OrderRequest;
import gift.dto.product.order.OrderResponse;
import gift.service.product.order.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @RequestBody @Valid OrderRequest request,
            HttpServletRequest servletRequest
    ) {
        Long memberId = (Long) servletRequest.getAttribute("memberId");

        OrderResponse response = orderService.placeOrder(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
