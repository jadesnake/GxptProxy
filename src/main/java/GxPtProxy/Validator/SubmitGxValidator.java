package GxPtProxy.Validator;

import GxPtProxy.Bean.Request.Gx;
import GxPtProxy.Bean.User;
import GxPtProxy.SessionManager;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class SubmitGxValidator extends BaseValidator {
    @Override
    public void validate(String method,Object[] args) {
        if(args.length<2){
            setError("0001","参数个数错误");
            return ;
        }
        if(args[0]==null){
            setError("0005","参数格式错误");
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
