package config.comp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("impl1")
@Slf4j
public class TestServiceImpl1 implements TestService {

    @Override
    public void sayHello() {
        log.info("123");
    }

    @Override
    public String sayHelloWithClass(String name) {
        log.info("1234");
        return "1234";
    }
}


