package com.encore.ordering.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerClass { //    공통 예외 처리

    //    프로그램 어디서든 controller단에서 예외가 터지면 잡아줌
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> entityNotFoundHandler(EntityNotFoundException e) {
        log.error("Handler EntityNotFoundException : " + e.getMessage());
        return this.errResMessage(HttpStatus.NOT_FOUND, e.getMessage()); // 404
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> illegalArgHandler(IllegalArgumentException e) {
        log.error("Handler IllegalArgumentException : " + e.getMessage());
        return this.errResMessage(HttpStatus.BAD_REQUEST, e.getMessage()); // 400
    }

    //    에러 공통화 동적으로 하기 - map형태의 메시지 커스텀
    private ResponseEntity<Map<String, Object>> errResMessage(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", Integer.toString(status.value()));
        body.put("error message", message);
        body.put("status message", status.getReasonPhrase());
        return new ResponseEntity<>(body, status);
    }
}
