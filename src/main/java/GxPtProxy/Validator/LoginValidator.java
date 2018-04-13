package GxPtProxy.Validator;

import GxPtProxy.GxptArea;
import java.lang.reflect.Field;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class LoginValidator extends BaseValidator{
    @Autowired
    private GxptArea gxptArea;

    @Override
    public void validate(String method,Object[] args) {
        if(!super.hasBadField(3,args)) {
            super.setError("0002","参数不能为空");
            return ;
        }
        //通过反射方式判断输入的地区参数是否正确
        String area = (String)args[1];
        try{
            Field field = GxptArea.class.getDeclaredField(area);
            field.setAccessible(true);
            //没找可以修改切面参数得方式只能后面再次改了
            args[1] = field.get(gxptArea);
        }
        catch(Exception e){
            super.setError("0003","地区参数不正确");
        }
    }
}
