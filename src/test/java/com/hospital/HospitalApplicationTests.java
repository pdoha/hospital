package com.hospital;

import com.hospital.member.Authority;
import com.hospital.member.entities.Authorities;
import com.hospital.member.entities.Member;
import com.hospital.member.repositories.AuthoritiesRepository;
import com.hospital.member.repositories.MemberRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HospitalApplicationTests {

	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private AuthoritiesRepository authoritiesRepository;

	@Test @Disabled
	void contextLoads() {
		//회원한명 불러와서
		Member member = memberRepository.findByUserId("user01").orElse(null);
		//권한 추가하고
		Authorities authorities = new Authorities();
		authorities.setMember(member);
		authorities.setAuthority(Authority.ADMIN); //관리자 권한추가
		authoritiesRepository.saveAndFlush(authorities); //저장
	}

}
