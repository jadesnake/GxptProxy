package GxPtProxy.Gxpt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="area")
@PropertySource("classpath:area.properties")
public class GxptArea {
    private String BEIJING;
    private String TIANJIN;
    private String HEBEI;
    private String SANXI;
    private String NEIMENG;
    private String LIAONING;
    private String DALIAN;
    private String JILIN;
    private String HEILJ;
    private String SHANGHAI;
    private String JIANGSHU;
    private String ZHEJIANG;
    private String NINGBO;
    private String ANHUI;
    private String FUJIAN;
    private String XIAMEN;
    private String JIANGXI;
    private String SANDONG;
    private String QINGDAO;
    private String HENAN;
    private String HUBEI;
    private String HUNAN;
    private String GUANGDONG;
    private String SHENZHEN;
    private String GUANGXI;
    private String HAINAN;
    private String CHONGQING;
    private String SHICHUAN;
    private String GUIZHOU;
    private String YUNNAN;
    private String XIZANG;
    private String SHANXI;
    private String GANSU;
    private String QINGHAI;
    private String NINGXIA;
    private String XINJIANG;
    public String getBEIJING() {
        return BEIJING;
    }

    public void setBEIJING(String BEIJING) {
        this.BEIJING = BEIJING;
    }

    public String getTIANJIN() {
        return TIANJIN;
    }

    public void setTIANJIN(String TIANJIN) {
        this.TIANJIN = TIANJIN;
    }

    public String getHEBEI() {
        return HEBEI;
    }

    public void setHEBEI(String HEBEI) {
        this.HEBEI = HEBEI;
    }

    public String getSANXI() {
        return SANXI;
    }

    public void setSANXI(String SANXI) {
        this.SANXI = SANXI;
    }

    public String getNEIMENG() {
        return NEIMENG;
    }

    public void setNEIMENG(String NEIMENG) {
        this.NEIMENG = NEIMENG;
    }

    public String getLIAONING() {
        return LIAONING;
    }

    public void setLIAONING(String LIAONING) {
        this.LIAONING = LIAONING;
    }

    public String getDALIAN() {
        return DALIAN;
    }

    public void setDALIAN(String DALIAN) {
        this.DALIAN = DALIAN;
    }

    public String getJILIN() {
        return JILIN;
    }

    public void setJILIN(String JILIN) {
        this.JILIN = JILIN;
    }

    public String getHEILJ() {
        return HEILJ;
    }

    public void setHEILJ(String HEILJ) {
        this.HEILJ = HEILJ;
    }

    public String getSHANGHAI() {
        return SHANGHAI;
    }

    public void setSHANGHAI(String SHANGHAI) {
        this.SHANGHAI = SHANGHAI;
    }

    public String getJIANGSHU() {
        return JIANGSHU;
    }

    public void setJIANGSHU(String JIANGSHU) {
        this.JIANGSHU = JIANGSHU;
    }

    public String getZHEJIANG() {
        return ZHEJIANG;
    }

    public void setZHEJIANG(String ZHEJIANG) {
        this.ZHEJIANG = ZHEJIANG;
    }

    public String getNINGBO() {
        return NINGBO;
    }

    public void setNINGBO(String NINGBO) {
        this.NINGBO = NINGBO;
    }

    public String getANHUI() {
        return ANHUI;
    }

    public void setANHUI(String ANHUI) {
        this.ANHUI = ANHUI;
    }

    public String getFUJIAN() {
        return FUJIAN;
    }

    public void setFUJIAN(String FUJIAN) {
        this.FUJIAN = FUJIAN;
    }

    public String getXIAMEN() {
        return XIAMEN;
    }

    public void setXIAMEN(String XIAMEN) {
        this.XIAMEN = XIAMEN;
    }

    public String getJIANGXI() {
        return JIANGXI;
    }

    public void setJIANGXI(String JIANGXI) {
        this.JIANGXI = JIANGXI;
    }

    public String getSANDONG() {
        return SANDONG;
    }

    public void setSANDONG(String SANDONG) {
        this.SANDONG = SANDONG;
    }

    public String getQINGDAO() {
        return QINGDAO;
    }

    public void setQINGDAO(String QINGDAO) {
        this.QINGDAO = QINGDAO;
    }

    public String getHENAN() {
        return HENAN;
    }

    public void setHENAN(String HENAN) {
        this.HENAN = HENAN;
    }

    public String getHUBEI() {
        return HUBEI;
    }

    public void setHUBEI(String HUBEI) {
        this.HUBEI = HUBEI;
    }

    public String getHUNAN() {
        return HUNAN;
    }

    public void setHUNAN(String HUNAN) {
        this.HUNAN = HUNAN;
    }

    public String getGUANGDONG() {
        return GUANGDONG;
    }

    public void setGUANGDONG(String GUANGDONG) {
        this.GUANGDONG = GUANGDONG;
    }

    public String getSHENZHEN() {
        return SHENZHEN;
    }

    public void setSHENZHEN(String SHENZHEN) {
        this.SHENZHEN = SHENZHEN;
    }

    public String getGUANGXI() {
        return GUANGXI;
    }

    public void setGUANGXI(String GUANGXI) {
        this.GUANGXI = GUANGXI;
    }

    public String getHAINAN() {
        return HAINAN;
    }

    public void setHAINAN(String HAINAN) {
        this.HAINAN = HAINAN;
    }

    public String getCHONGQING() {
        return CHONGQING;
    }

    public void setCHONGQING(String CHONGQING) {
        this.CHONGQING = CHONGQING;
    }

    public String getSHICHUAN() {
        return SHICHUAN;
    }

    public void setSHICHUAN(String SHICHUAN) {
        this.SHICHUAN = SHICHUAN;
    }

    public String getGUIZHOU() {
        return GUIZHOU;
    }

    public void setGUIZHOU(String GUIZHOU) {
        this.GUIZHOU = GUIZHOU;
    }

    public String getYUNNAN() {
        return YUNNAN;
    }

    public void setYUNNAN(String YUNNAN) {
        this.YUNNAN = YUNNAN;
    }

    public String getXIZANG() {
        return XIZANG;
    }

    public void setXIZANG(String XIZANG) {
        this.XIZANG = XIZANG;
    }

    public String getSHANXI() {
        return SHANXI;
    }

    public void setSHANXI(String SHANXI) {
        this.SHANXI = SHANXI;
    }

    public String getGANSU() {
        return GANSU;
    }

    public void setGANSU(String GANSU) {
        this.GANSU = GANSU;
    }

    public String getQINGHAI() {
        return QINGHAI;
    }

    public void setQINGHAI(String QINGHAI) {
        this.QINGHAI = QINGHAI;
    }

    public String getNINGXIA() {
        return NINGXIA;
    }

    public void setNINGXIA(String NINGXIA) {
        this.NINGXIA = NINGXIA;
    }

    public String getXINJIANG() {
        return XINJIANG;
    }

    public void setXINJIANG(String XINJIANG) {
        this.XINJIANG = XINJIANG;
    }

}