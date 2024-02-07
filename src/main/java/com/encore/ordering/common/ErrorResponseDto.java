package com.encore.ordering.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponseDto {
    //    에러 공통화 동적으로 하기 - map형태의 메시지 커스텀
    public static ResponseEntity<Map<String, Object>> makeMessage(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", Integer.toString(status.value()));
        body.put("error_message", message);
        body.put("status_message", status.getReasonPhrase());
        return new ResponseEntity<>(body, status);
    }
}
