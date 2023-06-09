package vuly.thesis.ecowash.core.payload.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RefreshTokenResponse {
    private String accessToken;
    private String tokenType = "Bearer";
}
