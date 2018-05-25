package GxPtProxy.Gxpt;

import Base.HttpUtils;
import GxPtProxy.Bean.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import io.netty.handler.codec.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Parser {
    static final Logger logger = LoggerFactory.getLogger(Parser.class);
    private static final String _YRZ   = "已认证" ;//已认证
    private static final String _CHOOSE_N = "no"  ; //未勾选
    private static final String _CHOOSE_Y = "yes" ; //已勾选
    private static final String _CHOOSE_S = "scan"; //扫描勾选
    /*
     * 原始数据格式：
     * {"key1":"01","key2":"0","key3":"201801=61=343784.73=1;201802=34=140601.98=2;201803=129=944751.65=1;201804=2=3335.92=0","key4":"1~0~0~~1~0~0~0~e6ab1507-008b-4ed1-9438-5c3d5b96e3ba","key5":"201804;20180515;201804","key6":"20170701-20180430"}
     * */
    public static MainCollect manCollect(String json) {
        MainCollect ret = new MainCollect();
        JSONObject root = JSON.parseObject(json);
        String key3 = root.getString("key3");
        String key5 = root.getString("key5");
        List<String> listVal = Splitter.on(';').splitToList(key3);
        for (String val : listVal) {
            List<String> inner = Splitter.on('=').splitToList(val);
            if (inner.size() > 0) {
                ret.addCollect(inner.get(0), inner.get(1), inner.get(2), inner.get(3));
            }
        }
        listVal = Splitter.on(';').splitToList(key5);
        ret.setDqssq(listVal.get(0));
        ret.setSsqjzrq(listVal.get(1));
        return ret;
    }
    public static boolean startConfirm(String json,RzHz cur,RzHz dq){
        JSONObject root = null;
        List<String> key2=null;
        try{
            root = JSON.parseObject(json);
            cur = new RzHz();
            dq = new RzHz();
            cur.setQrgxsl( root.getString("qrljcs") );
            dq.setQrgxsl( root.getString("qrljcs") );

            List<String> ssq = Splitter.on(';').splitToList(root.getString("ssq"));
            cur.setSsq( ssq.get(0) );
            dq.setSsq( ssq.get(0) );
            key2 = Splitter.on('*').splitToList(root.getString("key2"));
        }
        catch(JSONException e){
            return false;
        }
        if(cur==null||dq==null||key2==null){
            return  false;
        }
        for(int n=0;n<key2.size();n++){
            List<String> a = Splitter.on('~').splitToList(key2.get(n));
            if(a.size()==0 || a.size()!=19) continue;
            RzHz out = (n==0?cur:dq);
            out.setBcqrfpsl( a.get(0) );
            out.setBcyxgxsl( a.get(1) );
            out.setBcqrgxqrz( a.get(2) );
            out.setBcqrgxbkdk( a.get(3) );
            for(int nxt=4;nxt<a.size();){
                Statistics statistics = new Statistics();
                if(nxt==4)
                    statistics.setLabel(Statistics._FP_ZZZY);
                else if(nxt==7)
                    statistics.setLabel(Statistics._FP_JCZY);
                else if(nxt==10)
                    statistics.setLabel(Statistics._FP_HYZY);
                else if(nxt==13)
                    statistics.setLabel(Statistics._FP_HJ);
                else if(nxt==16)
                    statistics.setLabel(Statistics._FP_TXZY);
                statistics.setSl( a.get(nxt) );
                nxt += 1;
                statistics.setJe( a.get(nxt) );
                nxt += 1;
                statistics.setSe( a.get(nxt) );
                nxt += 1;
                out.addToGroups(statistics);
            }
        }
        return true;
    }

    //
    //处理抵扣统计数据
    //原始数据
    //01=34=1146135.96=140601.98=0=0=0=34=1146135.96=140601.98;
    //02=0=0=0=0=0=0=0=0=0;
    //03=0=0=0=0=0=0=0=0=0;
    //14=0=0=0=0=0=0=0=0=0;
    //99=34=1146135.96=140601.98=0=0=0=34=1146135.96=140601.98;
    //
    public static List<DkTj> DkTj(String key2) {
        List<DkTj> ret = new ArrayList<>();
        List<String> avg = Splitter.on(';').splitToList(key2);
        for(String one : avg){
            List<String> element = Splitter.on('=').splitToList(one);
            if(element.isEmpty())   break;
            DkTj dktj = new DkTj();

            Statistics gxGroup = new Statistics();
            Statistics smGroup = new Statistics();
            Statistics hjGroup = new Statistics();
            if(element.get(0).equals("01")){
                dktj.setLabel(Statistics._FP_ZZZY);
            }
            else if(element.get(0).equals("02")){
                dktj.setLabel(Statistics._FP_HYZY);
            }
            else if(element.get(0).equals("03")){
                dktj.setLabel(Statistics._FP_JCZY);
            }
            else if(element.get(0).equals("14")){
                dktj.setLabel(Statistics._FP_TXZY);
            }
            else if(element.get(0).equals("99")){
                dktj.setLabel(Statistics._FP_HJ);
            }
            if(!dktj.getLabel().isEmpty()){
                gxGroup.label = "勾选认证";
                gxGroup.sl = element.get(1);
                gxGroup.je = element.get(2);
                gxGroup.se = element.get(3);

                smGroup.label = "扫描认证";
                smGroup.sl = element.get(4);
                smGroup.je = element.get(5);
                smGroup.se = element.get(6);

                hjGroup.label = "合计";
                hjGroup.sl = element.get(7);
                hjGroup.je = element.get(8);
                hjGroup.se = element.get(9);

                dktj.addGroup(gxGroup);
                dktj.addGroup(smGroup);
                dktj.addGroup(hjGroup);
                ret.add(dktj);
            }
        }
        return ret;
    }
    public static List<Invoice> fromDk(String json){
        List<Invoice> ret = new ArrayList<>();
        JSONObject root = JSON.parseObject(json);
        JSONArray data = root.getJSONArray("aaData");
        for(Object one : data){
            Invoice invoice = new Invoice();
            JSONArray object = (JSONArray) one;
            invoice.setCode( object.getString(1) );
            invoice.setNumber( object.getString(2) );
            invoice.setKprq( object.getString(3) );
            invoice.setSalerMc( object.getString(4) );
            invoice.setAmount( object.getString(5) );
            invoice.setTaxAmount( object.getString(6) );
            if(object.getString(7).equals("勾选"))
                invoice.setChoose(_CHOOSE_Y);
            else
                invoice.setChoose(_CHOOSE_S);
            invoice.setState(_YRZ);
            invoice.setRzrq( object.getString(8) );
            ret.add(invoice);
        }
        return ret;
    }
    public static List<Invoice> fromGxRz(String json){
        List<Invoice> ret = new ArrayList<>();
        JSONObject root = JSON.parseObject(json);
        JSONArray data = root.getJSONArray("aaData");
        for(Object one : data){
            Invoice invoice = new Invoice();
            JSONArray object = (JSONArray)one;
            invoice.setCode( object.getString(1) );
            invoice.setNumber( object.getString(2) );
            invoice.setKprq( object.getString(3) );
            invoice.setSalerMc( object.getString(4) );
            invoice.setAmount( object.getString(5) );
            invoice.setTaxAmount( object.getString(6) );
            invoice.setChoose(_CHOOSE_Y);
            invoice.setState(_YRZ);
            invoice.setGxrq( object.getString(8)  );
            invoice.setRzrq(  object.getString(10) );
            ret.add(invoice);
        }
        return ret;
    }
    public static List<Invoice> fromGx(String json){
        List<Invoice> ret = new ArrayList<>();
        JSONObject root = JSON.parseObject(json);
        JSONArray data = root.getJSONArray("aaData");
        for(Object one : data){
            Invoice invoice = new Invoice();
            JSONArray object = (JSONArray)one;
            invoice.setCode( object.getString(1) );
            invoice.setNumber( object.getString(2) );
            invoice.setKprq( object.getString(3) );
            invoice.setSalerMc( object.getString(4) );
            invoice.setAmount( object.getString(5) );
            invoice.setTaxAmount( object.getString(6) );
            invoice.setState( ConvertStateCode(object.getString(7)) );
            if(object.getString(8).equals("0")){
                invoice.setChoose(_CHOOSE_N);
            }
            else  if(object.getString(8).equals("1")){
                invoice.setChoose(_CHOOSE_Y);
            }
            invoice.setGxrq(object.getString(9));
            //10勾选认证 12扫描认证
            if( object.getString(10).equals("1") ){
                invoice.setState(_YRZ);
            }
            if(object.get(11)!=null){
                invoice.setRzrq(  object.getString(11) );
            }
            if( object.size()>=12 ) {
                if( object.get(12)!=null && object.getString(12).equals("1")){
                    invoice.setChoose(_CHOOSE_S);
                    invoice.setState(_YRZ);
                }
            }
            if( object.size()>=13 && object.get(13)!=null){
                invoice.setRzrq(  object.getString(13) );
            }
            if( object.size()>=14 && object.get(14)!=null) {
                invoice.setSalerNo( object.getString(14) );
            }
            ret.add(invoice);
        }
        return ret;
    }
    /* 数据解析规则
	function changeLslb() {
	var e = $("#lsqslb").val().split("~"),
	t = e[2].split("*");
	本次为所属期-------begin
	共勾选发票
	$("#bcqrfpsl").text(t[0])
	有效勾选发票
	$("#bcyxgxsl").text(t[1])
	勾选且扫描认证发票
	$("#bcqrgxqrz").text(t[2])
	勾选不可抵扣发票
	$("#bcqrgxbkdk").text(t[3])
	增值税专用发票 数量 金额 税额
	$("#zpbcsl").text(t[4])	$("#zpbcje").text(t[5])	$("#zpbcse").text(t[6])
	机动车发票 数量 金额 税额
	$("#jdcbcsl").text(t[7]) $("#jdcbcje").text(t[8]) $("#jdcbcse").text(t[9])
	货运发票 数量 金额 税额
	$("#hybcsl").text(t[10]) $("#hybcje").text(t[11]) $("#hybcse").text(t[12])
	通行费发票 数量 金额 税额
	$("#txfbcsl").text(t[33]) $("#txfbcje").text(t[34]) $("#txfbcse").text(t[35])
	合计 数量 金额 税额
	$("#hjbcsl").text(t[13]), $("#hjbcje").text(t[14]), $("#hjbcse").text(t[15]);
	//截止本次勾选确认，共确认5 次，累计勾选34张发票，其中：
	var s = token.split("~");
	if("0" == s[0] || "5" == s[0] || "6" == s[0]){
		截止本次勾选确认，有效勾选发票
		$("#ljqryxgxsl").text(t[36])
	}
	else	{
		截止本次勾选确认，有效勾选发票
		$("#ljqryxgxsl").text(t[17])

		$("#ljqrfpsl").text(t[16])
		$("#ljqrgxqrz").text(t[18])
		$("#ljqrgxbkdk").text(t[19])
		累计有效勾选统计
		增值税专用发票 数量 金额 税额
		$("#zpljsl").text(t[20]) $("#zpljje").text(t[21]), $("#zpljse").text(t[22])
		机动车发票 数量 金额 税额
		$("#jdljcsl").text(t[23]) $("#jdljcje").text(t[24]) $("#jdljcse").text(t[25])
		货运发票 数量 金额 税额
		$("#hyljsl").text(t[26]) $("#hyljje").text(t[27]) $("#hyljse").text(t[28])
		合计 数量 金额 税额
		$("#hjljsl").text(t[29]) $("#hjljje").text(t[30]) $("#hjljse").text(t[31])

		"1" == t[32] ? $("#tjzt").html("当前状态：已完成") : $("#tjzt").html("当前状态：已提交")

		通行费发票 数量 金额 税额
		$("#txfljsl").text(t[36]) $("#txfljje").text(t[37]) $("#txfljse").text(t[38]);
	}
	}
	*/
    public static List<QrHz> fromQrHz(String key2){
        List<QrHz> ret = new ArrayList<>();
        if(key2.isEmpty())
            return ret;
        List<String> out1 = Splitter.on('=').splitToList(key2);
        for(String one : out1){
            QrHz qrHz = new QrHz();
            List<String> vals = Splitter.on('~').splitToList(one);
            qrHz.setQrcs(vals.get(1));
            qrHz.setQrtm(vals.get(0));
            List<String> perVal = Splitter.on('*').splitToList(vals.get(2));
            Statistics zpbc=null,jdcbc=null,hybc=null,txfbc=null,hjbc=null;
            if( perVal.size()>=35 ) {
                txfbc = new Statistics();
                txfbc.setSl(perVal.get(33));
                txfbc.setJe(perVal.get(34));
                txfbc.setSe(perVal.get(35));
                txfbc.enableLabel(false);
                qrHz.addCurGxTj(Statistics._FP_TXZY,txfbc);
            }
            if( perVal.size()>15 ){
                zpbc = new Statistics();
                zpbc.setSl(perVal.get(4));
                zpbc.setJe(perVal.get(5));
                zpbc.setSe(perVal.get(6));
                zpbc.enableLabel(false);
                qrHz.addCurGxTj(Statistics._FP_ZZZY,zpbc);

                jdcbc = new Statistics();
                jdcbc.setSl(perVal.get(7));
                jdcbc.setJe(perVal.get(8));
                jdcbc.setSe(perVal.get(9));
                jdcbc.enableLabel(false);
                qrHz.addCurGxTj(Statistics._FP_JCZY,jdcbc);

                hybc = new Statistics();
                hybc.setSl(perVal.get(10));
                hybc.setJe(perVal.get(11));
                hybc.setSe(perVal.get(12));
                hybc.enableLabel(false);
                qrHz.addCurGxTj(Statistics._FP_HYZY,hybc);

                hjbc = new Statistics();
                hjbc.setSl(perVal.get(13));
                hjbc.setJe(perVal.get(14));
                hjbc.setSe(perVal.get(15));
                hjbc.enableLabel(false);
                qrHz.addCurGxTj(Statistics._FP_HJ,hjbc);
            }
            if( perVal.size()>=20 && perVal.size()<=40 ){
                zpbc = new Statistics();
                zpbc.setSl(perVal.get(20));
                zpbc.setJe(perVal.get(21));
                zpbc.setSe(perVal.get(22));
                zpbc.enableLabel(false);
                qrHz.addCountGxTj(Statistics._FP_ZZZY,zpbc);

                jdcbc = new Statistics();
                jdcbc.setSl(perVal.get(23));
                jdcbc.setJe(perVal.get(24));
                jdcbc.setSe(perVal.get(25));
                jdcbc.enableLabel(false);
                qrHz.addCountGxTj(Statistics._FP_JCZY,jdcbc);

                hybc = new Statistics();
                hybc.setSl(perVal.get(26));
                hybc.setJe(perVal.get(27));
                hybc.setSe(perVal.get(28));
                hybc.enableLabel(false);
                qrHz.addCountGxTj(Statistics._FP_HYZY,hybc);

                hjbc = new Statistics();
                hjbc.setSl(perVal.get(29));
                hjbc.setJe(perVal.get(30));
                hjbc.setSe(perVal.get(31));
                hjbc.enableLabel(false);
                qrHz.addCountGxTj(Statistics._FP_HJ,hjbc);

                txfbc = new Statistics();
                txfbc.setSl(perVal.get(36));
                txfbc.setJe(perVal.get(37));
                txfbc.setSe(perVal.get(38));
                txfbc.enableLabel(false);
                qrHz.addCountGxTj(Statistics._FP_TXZY,txfbc);
            }
            ret.add(qrHz);
        }
        return  ret;
    }
    public static UserInfo fromQueryQy(String key2,String token){
        UserInfo userInfo = new UserInfo();
        if(key2.isEmpty())
            return userInfo;
        List<String> vals = Splitter.on('=').splitToList(key2);
        userInfo.setQymc( HttpUtils.decode(vals.get(0),"UTF-8") );
        if(vals.get(1).equals("0")){
            userInfo.setSbzq("月");
        }
        else{
            userInfo.setSbzq("季");
        }
        List<String> tkVals = Splitter.on('~').splitToList(token);
        if(tkVals.get(1).equals("1")){
            userInfo.setQylx("生产企业");
        }
        else if(tkVals.get(1).equals("2")){
            userInfo.setQylx("外贸企业");
        }
        else if(tkVals.get(1).equals("3")){
            userInfo.setQylx("外综服企业");
        }
        else{
            userInfo.setQylx("-");
        }
        userInfo.setOldsh( vals.get(7) );
        userInfo.setLevel( vals.get(8) );
        userInfo.setQysh( vals.get(10) );
        return userInfo;
    }
    public static String ConvertStateCode(String code){
        String ret = "";
        if(code.equals("0")){
            ret = "正常";
        }
        else if(code.equals("1")){
            ret = "失控";
        }
        else if(code.equals("2")){
            ret = "作废";
        }
        else if(code.equals("3")){
            ret = "红冲";
        }
        else if(code.equals("4")){
            ret = "异常";
        }
        else if(code.equals("5")){
            ret = "认证异常";
        }
        return ret;
    }
}
