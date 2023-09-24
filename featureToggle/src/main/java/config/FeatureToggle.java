package config;

import javax.lang.model.type.NullType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface FeatureToggle {
    String name() default "";

    /**
     * Set implementation clazz to be used.
     *
     * @return mock java class
     **/
    Class<?> alterClazz() default NullType.class;

    /**
     * Set implementation beanName to be used.
     *
     * @return target bean name
     */
    String alterBean() default "";

}
