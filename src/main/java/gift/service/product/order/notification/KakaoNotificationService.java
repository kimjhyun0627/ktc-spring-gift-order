package gift.service.product.order.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.entity.member.Member;
import gift.entity.product.order.Order;
import gift.external.kakao.message.KakaoMessageClient;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class KakaoNotificationService {

    private final KakaoMessageClient kakaoMessageClient;
    private final ObjectMapper objectMapper;

    public KakaoNotificationService(KakaoMessageClient kakaoMessageClient) {
        this.kakaoMessageClient = kakaoMessageClient;
        this.objectMapper = new ObjectMapper();
    }

    public void notifyOrderCompleted(Order order, Member member) {
        String templateJson = createOrderCompletionTemplate(order, member);
        kakaoMessageClient.sendToMe(member.getAccessToken(), templateJson);
    }

    private String createOrderCompletionTemplate(Order order, Member member) {
        Map<String, Object> message = new HashMap<>();
        message.put("object_type", "text");
        message.put(
                "text",
                String.format(
                        "🎁 주문 완료!\n상품: %s\n수량: %d개\n주문자: %s",
                        order.getOption().getName().name(),
                        order.getQuantity().amount(),
                        member.getEmail().email()
                )
        );

        Map<String, String> link = new HashMap<>();
        String url = "https://google.com";
        link.put("web_url", url);
        link.put("mobile_web_url", url);
        message.put("link", link);

        message.put("button_title", "주문 내역 보기");

        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("메시지 생성 실패", e);
        }
    }
}
