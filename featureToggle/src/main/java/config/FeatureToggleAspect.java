package config;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


@Slf4j
@Component("ffaspect")
public class FeatureToggleAspect implements MethodInterceptor {

    /**
     * Spring Application Context.
     */
    @Autowired
    private ApplicationContext appCtx;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        FeatureToggle featureToggle = getFeatureToggleAnnotation(invocation);
        if (featureToggle != null) {
            String s = featureToggle.name();
            Class<?> alterClazz = featureToggle.alterClazz();
            if (getExecutedClass(invocation).equals(alterClazz)) {
                return invocation.proceed();
            }

        }
        boolean isFeatureToggled = true;
        if (isFeatureToggled) {
            return invokeAlterClazz(invocation, featureToggle);
        }
        return invocation.proceed();
    }

    protected Class<?> getExecutedClass(MethodInvocation pMInvoc) {
        Class<?> executedClass = null;
        Object ref = pMInvoc.getThis();
        if (ref != null) {
            executedClass = AopUtils.getTargetClass(ref);
        }
        if (executedClass == null) {
            throw new IllegalArgumentException("ff4j-aop: Static methods cannot be feature flipped");
        }
        return executedClass;
    }

    protected FeatureToggle getFeatureToggleAnnotation(MethodInvocation mi) {
        Class<?> currentInterface = mi.getMethod().getDeclaringClass();
        if (AnnotatedElementUtils.hasAnnotation(currentInterface, FeatureToggle.class)) {
            return AnnotatedElementUtils.findMergedAnnotation(currentInterface, FeatureToggle.class);
        }
        return null;

    }

    protected Object invokeAlterClazz(final MethodInvocation mi, FeatureToggle ff) throws Throwable {
        Class<?> alterClazz = ff.alterClazz();
        Method method = mi.getMethod();
        Class<?> declaringClass = method.getDeclaringClass();
        try {
            // Spring context may have a bean of expected type and priority of get instance
            for (Object bean : appCtx.getBeansOfType(declaringClass).values()) {
                // Correct bean implementing the same class, or proxy of existing class
                if (AopUtils.isJdkDynamicProxy(bean) && ((Advised) bean).getTargetSource().getTarget().getClass().equals(alterClazz) ||
                        AopProxyUtils.ultimateTargetClass(bean).equals(alterClazz)) {
                    return mi.getMethod().invoke(bean, mi.getArguments());
                }
            }
            // Otherwise instanciate manually
            return mi.getMethod().invoke(ff.alterClazz(), mi.getArguments());
        } catch (IllegalAccessException e) {
            log.error("e1");
            throw e;
        } catch (InvocationTargetException invocationTargetException) {
            log.error("e2");
            throw invocationTargetException;
        } catch (Exception exception) {
            log.error("e3");
            throw exception;
        }
    }
}
