package com.encore.ordering.member.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class LoginReqDto {
    @NotEmpty(message = "email is essential")
    @Email(message = "email is not valid") // 이메일 형식에 어긋나면 메시지 전달
    private String email;
    @NotEmpty(message = "password is essential")
    @Size(min = 4, message = "minimum length is 4") // 최소 4자리
    private String password;
}
