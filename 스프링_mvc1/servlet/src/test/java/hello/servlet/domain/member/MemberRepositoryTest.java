package hello.servlet.domain.member;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MemberRepositoryTest {

	MemberRepository memberRepository=MemberRepository.getInstance();

	@AfterEach
	void afterEach(){
		memberRepository.clearStore();
	}

	@Test
	void save(){
		//given
		Member member=new Member("hello",20);

		//when
		Member save = memberRepository.save(member);

		//then
		Member byId = memberRepository.findById(save.getId());
		assertThat(byId).isEqualTo(save);
	}

	@Test
	void findAll(){
		//given
		Member member1 = new Member("member1", 20);
		Member member2 = new Member("member2", 30);

		memberRepository.save(member1);
		memberRepository.save(member2);

		List<Member> result=memberRepository.findAll();

		assertThat(result.size()).isEqualTo(2);
		assertThat(result).contains(member1,member2);
	}


}