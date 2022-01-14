package hello.core.beanfind;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import hello.core.AppConfig;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;

public class ApplicationContextBasicFindTest {

	AnnotationConfigApplicationContext ac=new AnnotationConfigApplicationContext(AppConfig.class);

	@Test
	@DisplayName("빈 이름으로 조회")
	void findBeanByName(){
		MemberService memberService=ac.getBean("memberService",MemberService.class);
		Assertions.assertEquals(memberService.getClass(), MemberServiceImpl.class);
	}

	@Test
	@DisplayName("이름 없이 타입만으로 조회")
	void findBeanByType(){
		MemberService memberService=ac.getBean(MemberService.class);
		assertThat(memberService).isInstanceOf(MemberService.class);
	}

	@Test
	@DisplayName("구체 타입으로 조회")
	void findBeanByName2(){
		MemberService memberService=ac.getBean("memberService",MemberServiceImpl.class);
		Assertions.assertEquals(memberService.getClass(), MemberServiceImpl.class);
	}

	@Test
	@DisplayName("빈 이름으로 조회X") void findBeanByNameX() {
		//ac.getBean("xxxxx", MemberService.class);
		Assertions.assertThrows(NoSuchBeanDefinitionException.class, () ->
			ac.getBean("xxxxx", MemberService.class));
	}
}
