package gift.service.product.order;

import gift.dto.product.order.OrderRequest;
import gift.dto.product.order.OrderResponse;
import gift.entity.member.Member;
import gift.entity.member.value.Role;
import gift.entity.product.option.ProductOption;
import gift.entity.product.order.Order;
import gift.exception.custom.InvalidOptionException;
import gift.repository.member.MemberRepository;
import gift.repository.product.option.ProductOptionRepository;
import gift.repository.product.order.OrderRepository;
import gift.repository.wish.WishRepository;
import gift.service.product.option.ProductOptionService;
import gift.service.product.order.notification.KakaoNotificationService;
import gift.service.wish.WishService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductOptionService productOptionService;
    private final ProductOptionRepository optionRepository;
    private final WishService wishService;
    private final KakaoNotificationService kakaoNotificationService;
    private final MemberRepository memberRepository;
    private final WishRepository wishRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            ProductOptionService productOptionService,
            ProductOptionRepository optionRepository,
            WishService wishService,
            KakaoNotificationService kakaoNotificationService,
            MemberRepository memberRepository,
            WishRepository wishRepository) {
        this.orderRepository = orderRepository;
        this.productOptionService = productOptionService;
        this.optionRepository = optionRepository;
        this.wishService = wishService;
        this.kakaoNotificationService = kakaoNotificationService;
        this.memberRepository = memberRepository;
        this.wishRepository = wishRepository;
    }

    @Override
    public OrderResponse placeOrder(Long memberId, OrderRequest request) {
        ProductOption option = optionRepository.findById(request.optionId())
                .orElseThrow(() -> new InvalidOptionException("상품 옵션이 존재하지 않습니다."));

        productOptionService.decreaseOptionAmount(
                option.getProduct().getId().id(),
                option.getId(),
                request.quantity(),
                Role.USER
        );

        Order order = Order.of(option, request.quantity(), request.message());
        Order saved = orderRepository.save(order);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        wishRepository.findByMemberIdAndProductId(memberId, option.getProduct().getId())
                .ifPresent(wish -> wishService.removeWish(wish.getId().id(), member));

        kakaoNotificationService.notifyOrderCompleted(saved, member);

        return OrderResponse.of(saved);
    }
}
