package Base;


import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;

@Component
public final class SpringUtil implements ApplicationContextAware{
    /** applicationContext. */
    private static ApplicationContext applicationContext;

    /**
     * @param applicationContext
     */
    protected static void initApplicationContext(ApplicationContext applicationContext) {
        SpringUtil.applicationContext = applicationContext;
    }
    /**
     * (non-Javadoc).
     *
     * @see ApplicationContextAware#setApplicationContext(ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringUtil.initApplicationContext(applicationContext);
    }

    public void destroy() {
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    // public static Object getBean(String name) {
    //     return applicationContext.getBean(name);
    // }
    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz){
       return getApplicationContext().getBean(clazz);
    }
    public static <T> T getBean(String name, Class<T> type) {
        return applicationContext.getBean(name, type);
    }

    public static String getMessage(String code, Object... args) {
        if(args==null)
            return null;
        return applicationContext.getMessage(code, args, null);
    }

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
    }
    public static void printContextBeans(){
        String[] beanNames =   SpringUtil.getApplicationContext().getBeanDefinitionNames();
        System.out.println("所有beanNames个数："+beanNames.length);
        for(String bn:beanNames){
            System.out.println(bn);
        }
    }
}
