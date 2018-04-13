package Base;

public class ResultFactory {
    public static Result Success(Object object){
        return new ResultImpl(object);
    }
    public static Result Success(){
        return new ResultImpl();
    }
    public static Result Failure(String code,String message){
        return new ResultImpl(false,code,message);
    }
}
