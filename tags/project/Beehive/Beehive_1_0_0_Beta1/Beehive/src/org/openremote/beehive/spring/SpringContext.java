package org.openremote.beehive.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;

/**
 * ApplicationContext for Spring container
 *
 * @author Dan 2009-2-16
 */
public class SpringContext {

    private static SpringContext m_instance;

    private static String[] contextFiles = new String[]{"spring-context.xml"};

    private ApplicationContext ctx;

    public SpringContext() {
        ctx = new ClassPathXmlApplicationContext(contextFiles);
    }

    public SpringContext(String[] setting) {
        ctx = new ClassPathXmlApplicationContext(setting);
    }

    /**
     * Gets a instance of <code>SpringContext</code>
     *
     * @return the instance of <code>SpringContext</code>
     */
    public synchronized static SpringContext getInstance() {
        if (m_instance == null) {
            m_instance = new SpringContext(contextFiles);
        }
        return m_instance;
    }

    /**
     * Gets a bean instance with the given bean identifier
     *
     * @param beanId the given bean identifier
     * @return a bean instance
     */
    public Object getBean(String beanId) {
        Object o = ctx.getBean(beanId);
        if (o instanceof TransactionProxyFactoryBean) {
            TransactionProxyFactoryBean factoryBean = (TransactionProxyFactoryBean) o;
            o = factoryBean.getObject();
        }
        return o;
    }

}
