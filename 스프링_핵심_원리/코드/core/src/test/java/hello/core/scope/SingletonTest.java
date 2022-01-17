package hello.core.scope;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Scope;

public class SingletonTest {
	@Test
	void singletonBeanFind(){


	}

	@Scope("singleton")
	static class SingletonBean{
		@PostConstruct
		public void init(){
			System.out.println("SingletonBean.inint");
		}

		@PreDestroy
		public void destroy(){
			System.out.println("SingletonBean.destroy");
		}
	}
}
