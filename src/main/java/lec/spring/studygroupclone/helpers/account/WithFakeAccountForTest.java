package lec.spring.studygroupclone.helpers.account;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithFakeAccountForTestFactory.class)
public @interface WithFakeAccountForTest {
    String email();
}
