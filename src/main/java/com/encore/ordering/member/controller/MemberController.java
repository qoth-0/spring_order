package com.encore.ordering.member.controller;

import com.encore.ordering.common.ResponseDto;
import com.encore.ordering.member.domain.Member;
import com.encore.ordering.member.dto.LoginReqDto;
import com.encore.ordering.member.dto.MemberCreateReqDto;
import com.encore.ordering.member.dto.MemberResDto;
import com.encore.ordering.member.service.MemberService;
import com.encore.ordering.securities.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/member/create")
//    @Valid : RequestBody로 들어오는 객체에 대한 검증(검증의 세부 설정은 MemberCreateReqDto에 정의), @RequestBody : json 형태로 데이터 받음
    public ResponseEntity<ResponseDto> memberCreate(@Valid @RequestBody MemberCreateReqDto memberCreateReqDto) {
        Member member = memberService.create(memberCreateReqDto);
//        ResponseEntity는 body와 HttpStatus로 구성
        return new ResponseEntity<>(new ResponseDto(HttpStatus.CREATED, "member successfully created", member.getId()), HttpStatus.CREATED);
    }

    @GetMapping("/members")
    public List<MemberResDto> members() {
        return memberService.findAll();
    }

    @GetMapping("/member/myInfo")
    public MemberResDto findMyInfo() { // 매개변수 없이 Authentication 객체에서 정보 가져오기
        return memberService.findMyInfo();
    }

//    @GetMapping("/member/{id}/orders") // 관리자용
//
//    @GetMapping("/member/myorders") // 사용자용

    @PostMapping("/doLogin")
    public ResponseEntity<ResponseDto> memberLogin(@Valid @RequestBody LoginReqDto loginReqDto) {
        Member member = memberService.login(loginReqDto);
//        토큰 생성(페이로드에 email, role 삽입)
        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());
        Map<String, Object> member_info = new HashMap<>();
        member_info.put("id", member.getId());
        member_info.put("token", jwtToken);
        return new ResponseEntity<>(new ResponseDto(HttpStatus.OK, "member successfully logined", member_info), HttpStatus.OK);
    }
}
