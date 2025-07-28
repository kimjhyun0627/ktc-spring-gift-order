package gift.entity.product.order;

import gift.entity.product.option.ProductOption;
import gift.entity.product.order.value.OrderMessage;
import gift.entity.product.order.value.OrderQuantity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private ProductOption option;

    @Embedded
    private OrderQuantity quantity;

    @Embedded
    private OrderMessage message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Order() {
    }

    private Order(ProductOption option, int quantity, String message) {
        this.option = option;
        this.quantity = new OrderQuantity(quantity);
        this.message = new OrderMessage(message);
        this.createdAt = LocalDateTime.now();
    }

    public static Order of(ProductOption option, int quantity, String message) {
        return new Order(option, quantity, message);
    }

    public Long getId() {
        return id;
    }

    public ProductOption getOption() {
        return option;
    }

    public OrderQuantity getQuantity() {
        return quantity;
    }

    public OrderMessage getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
