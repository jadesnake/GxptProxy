package GxPtProxy.Validator;

import GxPtProxy.Bean.User;
import GxPtProxy.Service.RedisService;
import GxPtProxy.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class UsualValidator extends BaseValidator {
    @Autowired
    private RedisService redisService;
    @Override
    public void validate(String method,Object[] args) {
        //最后一个参数为request
        if(!super.hasBadField(args.length-1,args)) {
            super.setError("0002","参数不能为空");
            return ;
        }
        HttpServletRequest httpServletRequest=null;
        for(Object object : args){
            if(object instanceof HttpServletRequest) {
                httpServletRequest = (HttpServletRequest)object;
            }
        }
    }
}
