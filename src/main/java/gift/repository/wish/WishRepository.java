package gift.repository.wish;

import gift.entity.product.value.ProductId;
import gift.entity.wish.Wish;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

    @EntityGraph(attributePaths = {"member", "product"})
    Page<Wish> findByMember_Id(Long memberId, Pageable pageable);

    Optional<Wish> findByMemberIdAndProductId(Long memberId, ProductId productId);
}
