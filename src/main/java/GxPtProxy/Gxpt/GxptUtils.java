package GxPtProxy.Gxpt;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GxptUtils {
    public static String GetTickCount(){
        String tick;
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        r.setSeed(System.currentTimeMillis());
        for (int i = 0; i < 21; i++) {
            sb.append( r.nextInt(9) );
        }
        sb.append("_");
        r.setSeed(System.currentTimeMillis());
        for (int i = 0; i < 13; i++) {
            sb.append( r.nextInt(9) );
        }
        tick = sb.toString();
        return tick;
    }
    public static String TakeJson(String data){
        int startPos = data.indexOf("{");
        int endPost = data.lastIndexOf("}");
        if(startPos!=-1 && endPost!=-1)
            return data.substring(startPos,endPost+1);
        return data;
    }
    public static String Request(String url,Map params,int timeout){
        String response="";
        try{
            response = Base.HttpUtils.post(url,params,CosplayIE(GetHost(url)),timeout);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    public static String Request(String url,Map params,int timeout,String cookie){
        String response="";
        try{
            response = Base.HttpUtils.post(url,params,CosplayIE(GetHost(url)),timeout,cookie);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    public static String CodeToError(String code){
        String ret;
        if(code.equals("00")) {
            ret = "请稍后再试";
        }
        else if(code.equals("98")) {
            ret = "网络调用异常，请重试";
        }
        else if(code.equals("99")) {
            ret = "网络调用超时";
        }
        else if(code.equals("101")) {
            ret = "数据库连接失败";
        }
        else if(code.equals("02")) {
            ret = "信息不存在";
        }
        else if(code.equals("03")) {
            ret = "系统异常";
        }
        else if(code.equals("04"))  {
            ret = "平台密码不正确";
        }
        else if(code.equals("05")) {
            ret = "平台密码错误次数超过十次，请联系税务机关解锁或明天再试";
        }
        else if( code.equals("09") ) {
            ret = "会话已超时，请重新登陆！";
        }
        else if(code.equals("11")) {
            ret = "您还未登录系统，请先登录";
        }
        else if( code.equals("12")) {
            ret = "请确认本企业是否属于取消认证政策的纳税人";
        }
        else if( code.equals("13") ) {
            ret = "特定企业不允许进行网上发票认证";
        }
        else if(code.equals("21")) {
            ret = "本平台启用状态为：未启用,无权登录此系统，请联系主管税务机关开通权限";
        }
        else if( code.equals("10") || code.equals("20") ) {
            ret = "信息出现异常，请稍后再试！";
        }
        else {
            ret = "未知异常";
        }
        return ret;
    }
    private static Map CosplayIE(String host){
        Map<String,String> header = new HashMap<>();
        header.put("Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
        header.put("x-requested-with","XMLHttpRequest");
        header.put("Accept-Language","zh-CN");
        header.put("Referer",host);
        header.put("Accept","text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01");
        header.put("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        header.put("Connection","Keep-Alive");
        header.put("Cache-Control","no-cache");
        return header;
    }
    private static String GetHost(String url){
        String https="https://";
        String http="http://";
        int startPos = 0;
        startPos = url.indexOf(https);
        if(startPos==-1) {
            startPos = url.indexOf(http);
            if(startPos==-1)
                return url;
            startPos += http.length();
        }
        else{
            startPos += https.length();
        }
        int endPos = url.indexOf("/",startPos);
        if(endPos!=-1)
            return url.substring(0,endPos);
        return url;
    }
}
