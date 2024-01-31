package com.encore.ordering.member.dto;

import com.encore.ordering.member.domain.Address;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class MemberCreateReqDto {
    @NotEmpty(message = "name is essential") // 빈값이면 에러 메시지 전달
    private String name;
    @NotEmpty(message = "email is essential")
    @Email(message = "email is not valid") // 이메일 형식에 어긋나면 메시지 전달
    private String email;
    @NotEmpty(message = "password is essential")
    @Size(min = 4, message = "minimum length is 4") // 최소 4자리
    private String password;
//    사용자한테 Address 객체를 받을 수 없으므로 Service에서 Address 객체로 조립
    private String city;
    private String street;
    private String zipcode;
}
