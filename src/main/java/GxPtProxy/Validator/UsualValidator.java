package GxPtProxy.Validator;

import GxPtProxy.Bean.User;
import GxPtProxy.SessionManager;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class UsualValidator extends BaseValidator {
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
        if(httpServletRequest!=null){
            User user = SessionManager.getUser(httpServletRequest);
            if(user.getTaxNo().isEmpty())
                super.setError("0003","请先登录");
        }
    }
}
