package GxPtProxy.Validator;

import com.google.common.base.Joiner;

import java.lang.reflect.Field;

public class FieldError {
    private String code =  "";    //错误代码
    private String message =  ""; //错误描述

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public FieldError(String code,String message){
        this.code = code;
        this.message = message;
    }
    public FieldError(){

    }
    public String toString(){
        String[] args = {this.code,this.message};
        return Joiner.on(",").join(args).toString();
    }
}
