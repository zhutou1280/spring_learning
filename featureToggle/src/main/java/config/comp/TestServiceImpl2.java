package config.comp;

import config.FeatureToggle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("impl2")
@Slf4j
public class TestServiceImpl2 implements TestService {

    public void sayHello() {
        log.info("test Impl2");
    }

    @Override
    public String sayHelloWithClass(String name) {
        return "4321";
    }
}
