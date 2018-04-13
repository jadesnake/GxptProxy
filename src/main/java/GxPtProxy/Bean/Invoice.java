package GxPtProxy.Bean;

public class Invoice {
    private String code=""; //代码
    private String number="";//号码
    private String kprq=""; //开票日期
    private String salerMc=""; //销名称
    private String salerNo=""; //销方税号
    private String amount=""; //金额
    private String taxAmount=""; //税额
    private String state=""; //状态
    private String choose=""; //勾选状态
    private String rzrq=""; //认证时间
    private String gxrq=""; //勾选日期

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getKprq() {
        return kprq;
    }

    public void setKprq(String kprq) {
        this.kprq = kprq;
    }

    public String getSalerMc() {
        return salerMc;
    }

    public void setSalerMc(String salerMc) {
        this.salerMc = salerMc;
    }

    public String getSalerNo() {
        return salerNo;
    }

    public void setSalerNo(String salerNo) {
        this.salerNo = salerNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getChoose() {
        return choose;
    }

    public void setChoose(String choose) {
        this.choose = choose;
    }

    public String getRzrq() {
        return rzrq;
    }

    public void setRzrq(String rzrq) {
        this.rzrq = rzrq;
    }

    public String getGxrq() {
        return gxrq;
    }

    public void setGxrq(String gxrq) {
        this.gxrq = gxrq;
    }
}
