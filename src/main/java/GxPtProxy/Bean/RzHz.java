package GxPtProxy.Bean;

import java.util.ArrayList;
import java.util.List;

public class RzHz {
    private String ssq="";  //所属期
    private String qrgxsl=""; //第几次确认勾选
    private String bcqrfpsl=""; //勾选发票数
    private String bcyxgxsl=""; //有效勾选发票
    private String bcqrgxqrz;	//勾选且扫描认证发票
    private String bcqrgxbkdk;	//勾选不可抵扣发票
    private List<Statistics> groups = new ArrayList<>();

    public String getSsq() {
        return ssq;
    }

    public void setSsq(String ssq) {
        this.ssq = ssq;
    }

    public String getQrgxsl() {
        return qrgxsl;
    }

    public void setQrgxsl(String qrgxsl) {
        this.qrgxsl = qrgxsl;
    }

    public String getBcqrfpsl() {
        return bcqrfpsl;
    }

    public void setBcqrfpsl(String bcqrfpsl) {
        this.bcqrfpsl = bcqrfpsl;
    }

    public String getBcyxgxsl() {
        return bcyxgxsl;
    }

    public void setBcyxgxsl(String bcyxgxsl) {
        this.bcyxgxsl = bcyxgxsl;
    }

    public String getBcqrgxqrz() {
        return bcqrgxqrz;
    }

    public void setBcqrgxqrz(String bcqrgxqrz) {
        this.bcqrgxqrz = bcqrgxqrz;
    }

    public String getBcqrgxbkdk() {
        return bcqrgxbkdk;
    }

    public void setBcqrgxbkdk(String bcqrgxbkdk) {
        this.bcqrgxbkdk = bcqrgxbkdk;
    }

    public List<Statistics> getGroups() {
        return groups;
    }

    public void setGroups(List<Statistics> groups) {
        this.groups = groups;
    }
    public void addToGroups(Statistics statistics){
        this.groups.add(statistics);
    }
}
