package GxPtProxy.Bean;

import java.util.HashMap;
import java.util.Map;

public class QrHz {
    private String qrcs;    //第几次确认
    private String qrtm;    //确认时间
    private Map<String,Statistics> curGxTj = new HashMap<>();   //本次有效勾选统计
    private Map<String,Statistics> countGxTj = new HashMap<>(); //累计有效勾选统计

    public String getQrcs() {
        return qrcs;
    }

    public void setQrcs(String qrcs) {
        this.qrcs = qrcs;
    }

    public String getQrtm() {
        return qrtm;
    }

    public void setQrtm(String qrtm) {
        this.qrtm = qrtm;
    }

    public Map<String, Statistics> getCurGxTj() {
        return curGxTj;
    }

    public Map<String, Statistics> getCountGxTj() {
        return countGxTj;
    }
    public void addCurGxTj(String key, Statistics tj){
        curGxTj.put(key,tj);
    }
    public void addCountGxTj(String key, Statistics tj){
        countGxTj.put(key,tj);
    }
}