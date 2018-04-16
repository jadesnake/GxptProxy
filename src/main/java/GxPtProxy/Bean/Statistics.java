package GxPtProxy.Bean;

import java.util.ArrayList;
import java.util.List;


public class Statistics {
    public String label=""; //标签
    public String sl="";    //数量
    public String se="";    //税额
    public String je="";    //金额
    //发票类型标签
    public static final String _FP_ZZZY = "增值税专用发票";
    public static final String _FP_HYZY = "货物运输业增值税专用发票";
    public static final String _FP_JCZY = "机动车销售统一发票";
    public static final String _FP_TXZY = "通行费发票";
    public static final String _FP_HJ = "合计";

    public Statistics(){
    }
    public Statistics(String label){
        this.label = label;
    }

    public void enableLabel(boolean b){
        label = (b ?  "" : null);
    }
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSl() {
        return sl;
    }

    public void setSl(String sl) {
        this.sl = sl;
    }

    public String getSe() {
        return se;
    }

    public void setSe(String se) {
        this.se = se;
    }

    public String getJe() {
        return je;
    }

    public void setJe(String je) {
        this.je = je;
    }
}
