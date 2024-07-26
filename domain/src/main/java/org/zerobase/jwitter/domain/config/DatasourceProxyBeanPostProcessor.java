package org.zerobase.jwitter.domain.config;

import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;

@Component
public class DatasourceProxyBeanPostProcessor implements BeanPostProcessor {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${spring.datasource.proxy-name}")
    private String DATA_SOURCE_PROXY_NAME;

    @Override
    public Object postProcessAfterInitialization(
            Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSource && !(bean instanceof ProxyDataSource)) {
            ProxyFactory factory = new ProxyFactory(bean);
            factory.setProxyTargetClass(true);
            factory.addAdvice(new ProxyDataSourceInterceptor((DataSource) bean));
            return factory.getProxy();
        }
        return bean;
    }

    private class ProxyDataSourceInterceptor implements MethodInterceptor {
        private final DataSource dataSource;

        ProxyDataSourceInterceptor(DataSource dataSource) {
            PrettyQueryEntryCreator creator = new PrettyQueryEntryCreator();
            creator.setMultiline(true);
            SLF4JQueryLoggingListener loggingListener = new SLF4JQueryLoggingListener();
            loggingListener.setQueryLogEntryCreator(creator);
            loggingListener.setLogLevel(SLF4JLogLevel.INFO);
            this.dataSource = ProxyDataSourceBuilder
                    .create(dataSource)
                    .name(DATA_SOURCE_PROXY_NAME)
                    .listener(loggingListener)
                    .afterMethod(executionContext -> {
                        Method method = executionContext.getMethod();
                        Class<?> targetClass = executionContext.getTarget().getClass();
                        log.debug("JDBC: " + targetClass.getSimpleName() + "#" + method.getName());
                    })
                    .afterQuery((execInfo, queryInfoList) -> {
                        log.info("Query took " + execInfo.getElapsedTime() +
                            "msec");
                    })
                    //.logQueryBySlf4j(SLF4JLogLevel.INFO)
                    .build();
        }
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            Method proxyMethod =
                    ReflectionUtils.findMethod(dataSource.getClass(),
                    invocation.getMethod().getName());

            if (proxyMethod != null) {
                return proxyMethod.invoke(dataSource,
                        invocation.getArguments());
            } else {
                return invocation.proceed();
            }

        }
    }

    private static class PrettyQueryEntryCreator extends DefaultQueryLogEntryCreator {
        private final Formatter formatter = FormatStyle.BASIC.getFormatter();

        @Override
        protected String formatQuery(String query) {
            return this.formatter.format(query);
        }
    }
}
