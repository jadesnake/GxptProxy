package GxPtProxy.Bean.Done;

import GxPtProxy.Bean.RzHz;

public class RzHzDone {
    private RzHz cur=null;
    private RzHz dq=null;
    private String token = "";
    private String ljhzxxfs="";
    private String signature="";

    public String getLjhzxxfs() {
        return ljhzxxfs;
    }

    public void setLjhzxxfs(String ljhzxxfs) {
        this.ljhzxxfs = ljhzxxfs;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public RzHz getCur() {
        return cur;
    }

    public void setCur(RzHz cur) {
        this.cur = cur;
    }

    public RzHz getDq() {
        return dq;
    }

    public void setDq(RzHz dq) {
        this.dq = dq;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
