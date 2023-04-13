package vuly.thesis.ecowash.core.payload.response;

import lombok.*;
import vuly.thesis.ecowash.core.util.MessageSourceUtil;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppResponse {
    private String message;
    private int code;
    private Object data;

    public static AppResponse success(Object data) {
        return  success(data, 2000);
    }

    public static AppResponse success(Object data, int status) {
        return AppResponse.builder()
                .code(status)
                .message(MessageSourceUtil.getInfoMessage(status, null))
                .data(data)
                .build();
    }

    public static AppResponse success() {
        return AppResponse.builder().code(2000).message(MessageSourceUtil.getInfoMessage(2000, null)).build();
    }
}
