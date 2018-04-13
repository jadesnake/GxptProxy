package GxPtProxy.Validator;

import com.google.common.base.Strings;
import Exception.ApiException;
public class BaseValidator implements Validator {
    private FieldError fieldError = new FieldError();
    public void setError(String code,String message){
        fieldError.setCode(code);
        fieldError.setMessage(message);
    }
    @Override
    public void clear(){
        fieldError.setCode("");
        fieldError.setMessage("");
    }
    protected boolean hasBadField(int count,Object[] args){
        boolean bRet = false;
        if( args.length < count  ){
            setError("0001","参数个数错误");
            return bRet;
        }
        try{
            boolean error = false;
            for(int i=0;i < count;i++){
                String var = (String)args[i];
                if(  Strings.isNullOrEmpty(var) ){
                    setError("0002","参数为空");
                    error = true;
                    break;
                }
            }
            if(error==false)
                bRet = true;
        }
        catch(Exception e){
            bRet = false;
        }
        return bRet;
    }
    public void doValidator(String method,Object[] args){
        validate(method,args);
        if(!this.fieldError.getCode().isEmpty()){
            throw new ApiException(this.fieldError.getCode(),this.fieldError.getMessage());
        }
    }
    public void validate(String method,Object[] args){

    }
}
