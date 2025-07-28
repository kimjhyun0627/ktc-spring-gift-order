package gift.external.kakao.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.entity.member.Member;
import gift.entity.product.order.Order;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoMessageClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public KakaoMessageClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void sendToMe(String accessToken, Order order, Member member) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", generateSelfMessage(order, member)); // 💡 JSON string

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(
                "https://kapi.kakao.com/v2/api/talk/memo/default/send",
                request,
                String.class
        );
    }

    private String generateSelfMessage(Order order, Member member) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("object_type", "text");
            message.put("text", "🎁 주문 완료!\n상품: %s\n수량: %d개\n주문자: %s"
                    .formatted(order.getOption().getName().name(), order.getQuantity().amount(),
                            member.getEmail().email()));

            Map<String, String> link = new HashMap<>();
            String url = "https://google.com";
            link.put("web_url", url);
            link.put("mobile_web_url", url);
            message.put("link", link);

            message.put("button_title", "주문 내역 보기");

            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            throw new RuntimeException("메시지 생성 실패", e);
        }
    }
}
