package sevenstar.marineleisure.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@CustomDataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public @interface MysqlDataJpaTest {
}
