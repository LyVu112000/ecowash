package vuly.thesis.ecowash.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vuly.thesis.ecowash.core.payload.response.AppResponse;
import vuly.thesis.ecowash.core.service.CommonService;

@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
public class CommonController {

    private final CommonService commonService;

    @GetMapping("/enum")
    public ResponseEntity<?> getEnum() {
        Object result = commonService.getEnum();
        return ResponseEntity.ok(AppResponse.success(result));
    }
}