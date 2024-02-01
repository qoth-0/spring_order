package com.encore.ordering.member.dto;

import com.encore.ordering.member.domain.Address;
import com.encore.ordering.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberResDto {
    private Long id;
    private String name;
    private String email;
    private String city;
    private String street;
    private String zipcode;
    private int orderCount;

//    fromDtoToEntity 공통화
    public static MemberResDto toMemberResDto(Member member) {
//        MemberResDtoBuilder(@Build 붙이면 사용 가능)로 필드마다 설정 가능 - null처리 용이
        MemberResDtoBuilder builder = MemberResDto.builder();
        builder.id(member.getId());
        builder.name(member.getName());
        builder.email(member.getEmail());
        builder.orderCount(member.getOrderings().size());
        Address address = member.getAddress();
        if(address != null) {
            builder.city(address.getCity());
            builder.street(address.getStreet());
            builder.zipcode(address.getZipcode());
        }
        return builder.build();
    }
}

