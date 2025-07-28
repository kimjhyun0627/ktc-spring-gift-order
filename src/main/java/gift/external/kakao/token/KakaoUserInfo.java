package gift.external.kakao.token;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfo(

        Long id,

        @JsonProperty("kakao_account")
        KakaoAccount kakaoAccount
) {

    public record KakaoAccount(
            String email
    ) {

    }
}
