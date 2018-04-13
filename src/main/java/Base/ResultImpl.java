package Base;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.fasterxml.jackson.annotation.JsonFilter;

import java.io.Serializable;

@JSONType(orders = {"success","code","message","data"})
public class ResultImpl implements Result,Serializable {
   private static final long serialVersionUID = 8065909381042531595L;

   private boolean success;

   private String code;

   private String message;

   private Object data;
   public ResultImpl() {
        this.success = true;
   }
   public ResultImpl(Object data) {
       this.success = true;
       this.data = data;
   }
   public ResultImpl(boolean success,String code,String message,Object data){
       this.success = success;
       this.code = code;
       this.message = message;
       this.data = data;
   }
   public ResultImpl(boolean success,String code,String message){
       this.success = success;
       this.code = code;
       this.message = message;
   }
    @JSONField(name = "success")
    public boolean IsSuccess() {
        return success;
    }
    @JSONField(name = "code")
    public String getCode() {
        return code;
    }
    @JSONField(name="message")
    public String getMessage() {
        return message;
    }
    @JSONField(name="data")
    public Object getData() {
        return data;
    }
}
