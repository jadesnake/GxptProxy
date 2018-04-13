package GxPtProxy.Controller;

import Base.ResultFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import Exception.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptAdvice {

    @ExceptionHandler(value = ApiException.class)
    @ResponseBody
    public Object apiExceptionHandler(HttpServletRequest req, Exception e){
        ApiException apiException = (ApiException)e;
        return ResultFactory.Failure(apiException.getCode(),apiException.getMsg());
    }
}
