package hello.core.autowired;

import java.lang.annotation.Annotation;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AutowiredTest {
	@Test
	void AutoWiredOption(){
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(
			TestBean.class);
	}

	static class TestBean{

	}
}
