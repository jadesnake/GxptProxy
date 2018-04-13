package GxPtProxy;

import GxPtProxy.Bean.DkTj;
import GxPtProxy.Bean.Invoice;
import GxPtProxy.Bean.MainCollect;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class Parser {
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
            DkTj.Group gxGroup = new DkTj.Group();
            DkTj.Group smGroup = new DkTj.Group();
            DkTj.Group hjGroup = new DkTj.Group();
            if(element.get(0).equals("01")){
                dktj.setLabel("增值税专用发票");
            }
            else if(element.get(0).equals("02")){
                dktj.setLabel("货物运输业增值税专用发票");
            }
            else if(element.get(0).equals("03")){
                dktj.setLabel("机动车销售统一发票");
            }
            else if(element.get(0).equals("14")){
                dktj.setLabel("通行费发票");
            }
            else if(element.get(0).equals("99")){
                dktj.setLabel("通行费发票");
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
            else  if(object.getString(12).equals("1")){
                invoice.setChoose(_CHOOSE_S);
            }
            invoice.setGxrq(object.getString(9));
            //10勾选认证 12扫描认证
            if( object.getString(10).equals("1") || object.getString(12).equals("1") ){
                invoice.setState(_YRZ);
            }
            if(object.get(11)!=null){
                invoice.setRzrq(  object.getString(11) );
            }
            else if(object.get(13)!=null){
                invoice.setRzrq(  object.getString(13) );
            }
            if(object.get(14)!=null)
                invoice.setSalerNo( object.getString(14) );
            ret.add(invoice);
        }
        return ret;
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
