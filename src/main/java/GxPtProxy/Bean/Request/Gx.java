package GxPtProxy.Bean.Request;

import java.util.ArrayList;
import java.util.List;

public class Gx {
    public static class Invoice{
        private String dm="";
        private String hm="";
        private String kprq="";
        private String zt="";

        public String getDm() {
            return dm;
        }
        public void setDm(String dm) {
            this.dm = dm;
        }

        public String getHm() {
            return hm;
        }
        public void setHm(String hm) {
            this.hm = hm;
        }

        public String getKprq() {
            return kprq;
        }
        public void setKprq(String kprq) {
            this.kprq = kprq;
        }

        public String getZt() {
            return zt;
        }
        public void setZt(String zt) {
            this.zt = zt;
        }
    }
    private String token="";
    private String taxNo="";
    private List<Invoice> params=new ArrayList<>();

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getTaxNo() {
        return taxNo;
    }
    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }

    public List<Invoice> getParams() {
        return params;
    }

    public void setParams(List<Invoice> params) {
        this.params = params;
    }
}
