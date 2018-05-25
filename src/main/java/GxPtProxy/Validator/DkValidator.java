package GxPtProxy.Validator;

import GxPtProxy.Bean.User;
import GxPtProxy.SessionManager;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class DkValidator extends BaseValidator {
    @Override
    public void validate(String method,Object[] args) {
        //最后一个参数为request
        if(!super.hasBadField(5,args)) {
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
