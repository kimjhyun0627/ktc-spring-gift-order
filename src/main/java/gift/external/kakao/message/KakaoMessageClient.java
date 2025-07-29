package gift.external.kakao.message;

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

    public KakaoMessageClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendToMe(String accessToken, String templateObjectJson) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("template_object", templateObjectJson);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            restTemplate.postForEntity(
                    "https://kapi.kakao.com/v2/api/talk/memo/default/send",
                    request,
                    String.class
            );
        } catch (Exception e) {
            throw new RuntimeException("카카오 메세지 전송 실패", e);
        }
    }
}
