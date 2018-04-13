package GxPtProxy.Bean;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainCollect {
    private String dqssq="";   //当前所属期
    private String ssqjzrq="";	//所属期截至日期
    private String token="";
    private List<Collect> collects = new ArrayList<>();

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDqssq() {
        return dqssq;
    }
    public void setDqssq(String dqssq) {
        this.dqssq = dqssq;
    }

    public String getSsqjzrq() {
        return ssqjzrq;
    }
    public void setSsqjzrq(String ssqjzrq) {
        this.ssqjzrq = ssqjzrq;
    }
    public void addCollect(String time,String count,String sehj,String zt){
        collects.add( new Collect(time,count,sehj,zt) );
    }
    public List<Collect> getCollects() {
        return collects;
    }
    public static class Collect{
        public String time="";  //时间
        public String count=""; //发票张数
        public String sehj="";  //税额合计
        public String zt="";    //状态 1-已申报 0-当前所属期 2-未申报
        public Collect(){
        }
        public Collect(String time,String count,String sehj,String zt){
            this.time = time;
            this.count= count;
            this.sehj = sehj;
            this.zt = zt;
        }
    }
}


