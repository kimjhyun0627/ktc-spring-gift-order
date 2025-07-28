package gift.dto.product.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
        @NotNull(message = "id는 null일 수 없습니다.") Long optionId,
        @Min(value = 1, message = "최소 1개 이상의 수량을 입력하세요.") int quantity,
        String message
) {

}
