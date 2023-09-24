package config.comp;

import config.FeatureToggle;

@FeatureToggle(name = "language-french", alterClazz = TestServiceImpl2.class)
public interface TestService {

    void sayHello();

    String sayHelloWithClass(String name);

}
