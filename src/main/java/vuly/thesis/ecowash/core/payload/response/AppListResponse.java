package vuly.thesis.ecowash.core.payload.response;


import lombok.*;
import vuly.thesis.ecowash.core.util.MessageSourceUtil;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppListResponse<T> {
    private String message;
    private int code = 2000;
    private List<T> data;
    private int totalPages;
    private long totalElements;

    public AppListResponse(List<T> data, int totalPages, long totalElements) {
        this.data = data;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.code = 2000;
        this.message = MessageSourceUtil.getInfoMessage(2000, null);
    }
}
