package GxPtProxy.Validator;

import Base.SpringUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import Exception.*;

import java.lang.reflect.Method;

@Component
@Aspect
public class CheckParam {
   @Pointcut(value="@annotation(GxPtProxy.Validator.ParamValidator)")
   public void CheckParamPointcut(){

   }
   @Before("CheckParamPointcut()")
   public void CheckParam(JoinPoint point) throws Throwable{
       //  获得切入目标对象
       Object target = point.getThis();
       // 获得切入方法参数
       Object [] args = point.getArgs();
       // 获得切入的方法
       Method method = ((MethodSignature)point.getSignature()).getMethod();
       String mehtodName="";
       //获取注解中配置的实现类
       Class validatorClass = null;
       try{
           validatorClass = method.getAnnotation(ParamValidator.class).validatorClass();
           mehtodName = method.getName();
       }
       catch(Exception e){

       }
       //注解中配置的实现类不为空时，执行此实现类中的校验
       if(validatorClass != null){
           BaseValidator validator = null;
           try {
               validator = (BaseValidator)SpringUtil.getBean(validatorClass);
               if(validator==null){
                   validator = (BaseValidator)validatorClass.newInstance();
               }
           } catch (Exception e) {
               throw new ApiException("-1","未知错误");  // 抛出异常，交给上层处理
           }
           validator.clear();
           validator.doValidator(mehtodName,args);
           //point.proceed(args);
       }
   }

}
