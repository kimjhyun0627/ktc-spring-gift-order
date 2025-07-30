package gift.fixture;

import gift.entity.product.Product;
import gift.entity.product.option.ProductOption;

public final class ProductOptionFixture {

    private ProductOptionFixture() {
    }

    public static ProductOption create(
            Long id,
            Product product,
            String name,
            int quantity
    ) {
        ProductOption option = ProductOption.of(
                product,
                name,
                quantity
        );
        return withId(option, id);
    }

    public static ProductOption createNoId(
            Product product,
            String name,
            int quantity,
            boolean hidden
    ) {
        return ProductOption.of(
                product,
                name,
                quantity
        );
    }

    public static ProductOption visible(
            Long id,
            String name,
            int quantity
    ) {
        return create(id, ProductFixture.visible(), name, quantity);
    }

    public static ProductOption hidden(
            Long id,
            String name,
            int quantity
    ) {
        return create(id, ProductFixture.visible(), name, quantity);
    }

    public static ProductOption visible() {
        return visible(1L, "옵션A", 5);
    }

    public static ProductOption hidden() {
        return hidden(1L, "옵션A", 5);
    }

    private static ProductOption withId(ProductOption option, Long id) {
        try {
            java.lang.reflect.Field field = ProductOption.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(option, id);
            return option;
        } catch (Exception e) {
            throw new RuntimeException("ProductOption ID 설정 실패", e);
        }
    }
}
