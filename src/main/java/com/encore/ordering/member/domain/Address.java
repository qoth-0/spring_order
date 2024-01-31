package com.encore.ordering.member.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable // 삽입가능한 객체라는 의미
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address { // Member의 주소
    private String city;
    private String street;
    private String zipcode;
}
