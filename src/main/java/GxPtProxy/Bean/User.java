package GxPtProxy.Bean;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = -1476867075314950106L;
    private String taxNo="";
    private String token="";
    private String host="";
    private String dqrq="";
    private String cookssq="";

    public String getCookssq() {
        return cookssq;
    }

    public void setCookssq(String cookssq) {
        this.cookssq = cookssq;
    }

    public String getDqrq() {
        return dqrq;
    }

    public void setDqrq(String dqrq) {
        this.dqrq = dqrq;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
