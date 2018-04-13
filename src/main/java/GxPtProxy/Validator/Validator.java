package GxPtProxy.Validator;

public interface Validator {
    public void clear();
    public void validate(String method,Object[] args);
}
