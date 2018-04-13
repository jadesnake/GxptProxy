package Exception;

public class ApiException extends RuntimeException {
    private String code;

    private String msg;

    private Object[] args;

    public ApiException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ApiException(String code, Object... args) {
        this.code = code;
        this.args = args;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + code;
    }
}
