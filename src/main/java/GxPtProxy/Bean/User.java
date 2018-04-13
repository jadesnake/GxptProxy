package GxPtProxy.Bean;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = -1476867075314950106L;
    private String taxNo="";
    private String token="";
    private String host="";

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
