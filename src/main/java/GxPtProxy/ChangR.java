package GxPtProxy;

import Base.HttpUtils;
import GxPtProxy.Bean.Query;
import GxPtProxy.Bean.Request.Gx;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import io.netty.handler.codec.json.JsonObjectDecoder;
import org.omg.CORBA.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ChangR {
    protected static final Logger logger = LoggerFactory.getLogger(ChangR.class);
    private static final String _SUCCESS = "SUCCESS";
    private static final String _EMPTY = "EMPTY";
    public enum RESULT{
         SUCCESS,ERROR,NEXT_LOGIN,MAKE_SECRET;
    }
    public class Secret{
        public String ts="";
        public String page="";
    }
    public class Data{
        public String nsrmc="";
        public String dqrq="";
        public String packet="";
        public String random="";
        public String gxrqfw="";
        public String cookssq="";
        public String ljrzs="";
    }

    private String token="";
    private String lastMsg="";
    private String ymbb="";
    private String rpJson="";
    private String host="";
    private String taxNo="";
    private Data data = new Data();
    private Secret secret = new Secret();

    private String GetTickCount(){
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
    private String GetHost(String url){
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
    private String TakeJson(String data){
       int startPos = data.indexOf("{");
       int endPost = data.lastIndexOf("}");
       if(startPos!=-1 && endPost!=-1)
           return data.substring(startPos,endPost+1);
       return data;
    }
    private Map CosplayIE(String host){
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
    private String Request(String url,Map params,int timeout){
        String response="";
        try{
            response = Base.HttpUtils.post(url,params,CosplayIE(GetHost(url)),timeout);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    private String Request(String url,Map params,int timeout,String cookie){
        String response="";
        try{
            response = Base.HttpUtils.post(url,params,CosplayIE(GetHost(url)),timeout,cookie);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    private String CodeToError(String code){
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
    private <T extends Query> String BuildPagerJson(T query,int columns){
        String sColumns="";
        JSONArray jsonRq = new JSONArray();
        for(int n=1;n<columns;n++){
            sColumns+=',';
        }
        for(int n=0;n<5;n++) {
            JSONObject val = new JSONObject();
            switch(n) {
                case 0:
                    val.put("name","sEcho");
                    val.put("value",1);
                    break;
                case 1:
                    val.put("name","iColumns");
                    val.put("value",columns);
                    break;
                case 2:
                    val.put("name","sColumns");
                    val.put("value",sColumns);
                    break;
                case 3:
                    Integer page = Integer.valueOf(query.page);
                    Integer max  = Integer.valueOf(query.max);
                    val.put("name","iDisplayStart");
                    val.put("value",page*max);
                    break;
                case 4:
                    val.put("name","iDisplayLength");
                    val.put("value",query.max);
                    break;
            }
            jsonRq.add(val);
        }
        for(int n=0;n<columns;n++) {
            JSONObject val = new JSONObject();
            StringBuilder sb = new StringBuilder();
            sb.append("mDataProp_");
            sb.append(n);
            val.put("name",sb.toString());
            val.put("value",n);
            jsonRq.add(val);
        }
        return jsonRq.toJSONString();
    }
    public RESULT Login(String hello) {
        String url = host;
        url += "/SbsqWW/login.do?callback=jQuery";
        url += GetTickCount();
        Map<String,String> params = new HashMap<>();
        params.put("type","CLIENT-HELLO");
        params.put("ymbb",ymbb);
        params.put("clientHello",hello);
        String response = Request(url,params,15000);
        if(response==null||response.isEmpty()){
            lastMsg = _EMPTY;
            return RESULT.ERROR;
        }
        rpJson = TakeJson(response);
        JSONObject jsonObj = null;
        String key1="";
        try{
            jsonObj = JSON.parseObject(rpJson);
            key1 = jsonObj.getString("key1");
            if( key1.isEmpty() ) {
                lastMsg = "Login lost key1";
                return RESULT.ERROR;
            }
            token = jsonObj.getString("key2");
            if(key1.equals("03")){
                lastMsg = _SUCCESS;
                data.nsrmc = HttpUtils.decode(jsonObj.getString("key3"),"GBK");
                data.dqrq = jsonObj.getString("key4");
                return RESULT.SUCCESS;
            }
            if(key1.equals("01")){
                data.packet = jsonObj.getString("key2");
                data.random = jsonObj.getString("key3");
                lastMsg = _SUCCESS;
                return RESULT.NEXT_LOGIN;
            }
        }
        catch(JSONException e){
            e.printStackTrace();
            lastMsg = "Login json error";
            return RESULT.ERROR;
        }
        lastMsg = CodeToError(key1);
        return RESULT.ERROR;
    }
    public RESULT SecondLogin(String authCode,String random,MakeSecret secret){
        if(RESULT.SUCCESS!=querySecret("01")){
            lastMsg = "请求加密参数失败";
            return RESULT.ERROR;
        }
        if(secret==null){
            lastMsg = "加密实现为空";
            return  RESULT.ERROR;
        }
        String publickey = secret.checkTaxno(taxNo,this.secret.ts,"",this.secret.page,random);
        int n=0;
        do{
            RESULT result = retryLogin(authCode,random,publickey);
            if(result==RESULT.SUCCESS){
                return result;
            }
            if(lastMsg.equals("无此用户")) {
                return  result;
            }
            n += 1;
        }while (n<3);
        return RESULT.ERROR;
    }
    public RESULT submitGx(Gx gx,MakeSecret secret){
        String url = host;
        url += "/SbsqWW/gxtj.do?callback=jQuery";
        url += GetTickCount();

        String key1="";
        StringBuilder fpdms = new StringBuilder();
        StringBuilder fphms = new StringBuilder();
        StringBuilder kprqs = new StringBuilder();
        StringBuilder zts = new StringBuilder();
        for(Gx.Invoice one : gx.getParams() ){
            if(one==null) continue;
            fpdms.append(one.getDm());
            fpdms.append('=');

            fphms.append(one.getHm());
            fphms.append('=');

            kprqs.append(one.getKprq());
            kprqs.append('=');

            if(one.getZt().equals("yes"))
                zts.append("1");
            else
                zts.append("0");
            zts.append("=");
        }
        if(fpdms.length()!=0)
            fpdms.deleteCharAt( fpdms.length()-1 );
        if(fphms.length()!=0)
            fphms.deleteCharAt( fphms.length()-1 );
        if(kprqs.length()!=0)
            kprqs.deleteCharAt( kprqs.length()-1 );
        if(zts.length()!=0)
            zts.deleteCharAt( zts.length()-1 );
        if(fpdms.length()==0&&fphms.length()==0&&kprqs.length()==0&&zts.length()==0){
            lastMsg = "参数错误";
            return  RESULT.ERROR;
        }
        if(secret==null){
            lastMsg = "参数错误";
            return  RESULT.ERROR;
        }
        if(RESULT.SUCCESS!=querySecret("02")){
            lastMsg = "请求加密参数异常";
            return  RESULT.ERROR;
        }
        String pubkey = secret.checkInvConf(taxNo,token,this.secret.ts,"",this.secret.page);

        Map<String,String> params = new HashMap<>();
        params.put("fpdm",fpdms.toString());
        params.put("fphm",fphms.toString());
        params.put("kprq",kprqs.toString());
        params.put("zt",zts.toString());
        params.put("cert",taxNo);
        params.put("token",token);
        params.put("ymbb",ymbb);
        params.put("ts",this.secret.ts);
        params.put("publickey",pubkey);
        String response = Request(url,params,15000);
        if(response==null||response.isEmpty()) {
            lastMsg = _EMPTY;
            return RESULT.ERROR;
        }
        rpJson = TakeJson(response);
        try{
            JSONObject root = JSON.parseObject(rpJson);
            key1 = root.getString("key1");
            if(key1.equals("000"))
            {
                token = root.getString("key2");
                lastMsg = _SUCCESS;
                return RESULT.SUCCESS;
            }
        }
        catch(JSONException e){
            e.printStackTrace();
            lastMsg = "submitGx json error";
            return  RESULT.ERROR;
        }
        RESULT ret = RESULT.ERROR;
        if(key1.equals("001")) {
            lastMsg = "数据保存失败！";
        }
        else
            lastMsg = CodeToError(key1);
        return ret;
    }
    public RESULT startConfirmGx(String nsrmc){
        if(this.data.cookssq.isEmpty() || this.data.gxrqfw.isEmpty()){
            if(RESULT.SUCCESS!=beforeConfirmGx()){
                return RESULT.ERROR;
            }
        }
        if(this.data.ljrzs.isEmpty() || this.data.dqrq.isEmpty()){
            if(RESULT.SUCCESS!=queryQrgx())
                return RESULT.ERROR;
        }
        StringBuilder cookie = new StringBuilder();
        cookie.append("dqrq=");
        cookie.append(data.dqrq);
        cookie.append(";");
        cookie.append("nsrmc=");
        cookie.append(HttpUtils.encode(nsrmc));
        cookie.append(";");
        cookie.append("skssq=");
        cookie.append(this.data.cookssq);
        cookie.append(";");
        cookie.append("gxrqfw=");
        cookie.append(this.data.gxrqfw);
        cookie.append(";");
        cookie.append("sxy=0002;");
        cookie.append("token=");
        cookie.append(HttpUtils.encode(token));

        String url = host;
        url += "/SbsqWW/qrgx.do?callback=jQuery";
        url += GetTickCount();
        Map<String,String> params = new HashMap<>();
        params.put("id","querysbzt");
        params.put("id","querysbzt");
        params.put("key1",taxNo);
        params.put("key2",token);
        params.put("ymbb",ymbb);
        String response = Request(url,params,15000,cookie.toString());
        rpJson = TakeJson(response);
        String key1="",key2="";
        try{
            JSONObject root = JSON.parseObject(rpJson);
            key1 = root.getString("key1");
            key2 = root.getString("key2");
            if(key1.equals("01") && (key2.equals("3")||key2.equals("1")||key2.equals("4")) )
            {
                //second

            }
        }
        catch(JSONException e){
            lastMsg = "startConfirmGx json error";
            return RESULT.ERROR;
        }
        if(key2.equals("0")) {
            lastMsg = "申报状态出现异常，请稍后再试";
        }
        else if(key2.equals("2")){
            lastMsg = "税款所属期的申报工作已完成，本批次发票请您在下期进行勾选认证操作";
        }
        else if(key2.equals("2")){
            lastMsg = "税款所属期的申报工作已完成，本批次发票请您在下期进行勾选认证操作";
        }
        else{
            lastMsg = CodeToError(key1);
        }
        return RESULT.ERROR;
    }
    public RESULT queryInfo(){
        String url = host;
        url += "/SbsqWW/nsrcx.do?callback=jQuery";
        url += GetTickCount();

        Map<String,String> params = new HashMap<>();
        params.put("cert",taxNo);
        params.put("token",token);
        params.put("ymbb",ymbb);
        String response = Request(url,params,15000);
        if(response==null || response.isEmpty()){
            lastMsg = _EMPTY;
            return RESULT.ERROR;
        }
        rpJson = TakeJson(response);
        String key1="";
        try{
           JSONObject root = JSON.parseObject(rpJson);
           key1 = root.getString("key1");
           if(key1.equals("01")) {
               rpJson = root.getString("key2");
               token = root.getString("key3");
               lastMsg = _SUCCESS;
               return RESULT.SUCCESS;
           }
        }
        catch(JSONException e){
            e.printStackTrace();
            lastMsg = "queryInfo json error";
            return RESULT.ERROR;
        }
        lastMsg = CodeToError(key1);
        RESULT ret = RESULT.ERROR;
        return ret;
    }
    public RESULT quit(){
        String url = host;
        url += "/SbsqWW/quit.do?callback=jQuery";
        url += GetTickCount();

        Map<String,String> params = new HashMap<>();
        params.put("cert",taxNo);
        String response = Request(url,params,15000);
        if(response==null||response.isEmpty()){
            lastMsg = _EMPTY;
            return RESULT.ERROR;
        }
        rpJson = TakeJson(response);
        String key1="";
        try{
            JSONObject root = JSON.parseObject(rpJson);
            key1 = root.getString("key1");
        }
        catch(JSONException e){
            e.printStackTrace();
            lastMsg = "Quit json error";
            return RESULT.ERROR;
        }
        RESULT ret;
        if(key1.equals("01")) {
            ret = RESULT.SUCCESS;
            lastMsg = _SUCCESS;
        }
        else {
            ret = RESULT.ERROR;
            lastMsg = CodeToError(key1);
        }
        return ret;
    }
    public RESULT mainCollectByYear(String year){
        String url = host;
        url += "/SbsqWW/qrgycx.do?callback=jQuery";
        url += GetTickCount();

        Map<String,String> params = new HashMap<>();
        params.put("cert",taxNo);
        params.put("token",token);
        params.put("ymbb",ymbb);
        String response = Request(url,params,15000);
        if(response==null||response.isEmpty()){
            lastMsg = _EMPTY;
            return RESULT.ERROR;
        }
        rpJson = TakeJson(response);
        String key1="";
        try{
            JSONObject root = JSON.parseObject(rpJson);
            key1 = root.getString("key1");
            if(key1.equals("01")){
                token = root.getString("key4");
            }
        }
        catch(JSONException e){
            e.printStackTrace();
            lastMsg = "mainCollectByYear json error";
            return RESULT.ERROR;
        }
        RESULT ret;
        if(key1.equals("01")){
            ret = RESULT.SUCCESS;
            lastMsg = _SUCCESS;
        }
        else{
            ret = RESULT.ERROR;
            CodeToError(key1);
        }
        return ret;
    }
    public RESULT queryFromGxRz(Query.GxRz gxRz){
        String key1="";

        String aoData = BuildPagerJson(gxRz,11);
        String url = host;
        url += "/SbsqWW/qrgx.do?callback=jQuery";
        url += GetTickCount();

        Map<String,String> params = new HashMap<>();
        params.put("id","queryqrjg");
        if(gxRz.state==Query.GxRz.State.GX_N_QR){
            params.put("qrzt","1");
        }
        else{
            params.put("qrzt","2");
        }
        params.put("key1",taxNo);
        params.put("key2",token);
        params.put("aoData",aoData);
        params.put("ymbb",ymbb);
        String response = Request(url,params,15000);
        rpJson = TakeJson(response);
        try{
            JSONObject root = JSON.parseObject(rpJson);
            key1 = root.getString("key1");
            if(key1.equals("01")){
                token = root.getString("key3");
                rpJson = root.getString("key2");
                lastMsg = _SUCCESS;
                return RESULT.SUCCESS;
            }
            if(root.getString("key4").equals("0")){
                lastMsg="没有符合条件的记录";
                return RESULT.ERROR;
            }
        }
        catch(JSONException e){
            lastMsg = "queryFromGxRz json error";
            return RESULT.ERROR;
        }
        if(key1.equals("00")){
            lastMsg = "查询发票信息出现异常，请稍后再试！";
        }
        else {
            lastMsg = CodeToError(key1);
        }
        RESULT ret = RESULT.ERROR;
        return ret;
    }
    public RESULT queryFromQrgx(String date){
        String key1="";

        String url = host;
        url += "/SbsqWW/qrgx.do?callback=jQuery";
        url += GetTickCount();

        Map<String,String> params = new HashMap<>();
        params.put("id","querysbzt");
        params.put("key1",taxNo);
        params.put("key2",token);
        params.put("ymbb",ymbb);
        params.put("ssq",date);
        String response = Request(url,params,15000);
        rpJson = TakeJson(response);
        try{
            JSONObject root = JSON.parseObject(rpJson);
            key1 = root.getString("key1");
            if(key1.equals("000"))
            {
                lastMsg = _SUCCESS;
                token = root.getString("key3");
                return RESULT.SUCCESS;
            }
        }
        catch(JSONException e){
            lastMsg = "Request json error";
            return RESULT.ERROR;
        }
        RESULT ret = RESULT.ERROR;
        if(key1.equals("001")){
            lastMsg="数据获取失败";
        }
        else if(key1.equals("09")){
            lastMsg="会话已超时，请重新登陆！";
        }
        else if(key1.equals("98")){
            lastMsg="外网调用内网异常，请重试！";
        }
        else
            lastMsg = CodeToError(key1);
        return ret;
    }
    public RESULT queryDk(Query.Dkcx dkcx){
        String key1="";
        String aoData = BuildPagerJson(dkcx,11);

        String url = host;
        url += "/SbsqWW/dktj.do?callback=jQuery";
        url += GetTickCount();

        Map<String,String> params = new HashMap<>();
        params.put("aoData",aoData);
        params.put("ymbb",ymbb);
        params.put("cert",taxNo);
        params.put("token",token);
        params.put("oper","cx");
        params.put("fpdm","");
        params.put("fphm","");
        params.put("xfsbh",dkcx.xfsbh);
        params.put("qrrzrq_q",dkcx.qrrzrq_q);
        params.put("qrrzrq_z",dkcx.qrrzrq_z);
        params.put("fply","0");

        String tjyf = dkcx.tjyf.replaceAll("-*","");
        tjyf = tjyf.substring(0,6);
        params.put("tjyf",tjyf);
        String response = Request(url,params,15000);
        if(response==null||response.isEmpty()){
            lastMsg = _EMPTY;
            return RESULT.ERROR;
        }
        rpJson = TakeJson(response);
        try{
            JSONObject root = JSON.parseObject(rpJson);
            key1 = root.getString("key1");
            if(key1.equals("01")) {
                lastMsg = _SUCCESS;
                token = root.getString("key3");
                rpJson = root.getString("key2");
                return RESULT.SUCCESS;
            }
        }
        catch(JSONException e){
            lastMsg = "queryDk json error";
            return RESULT.ERROR;
        }
        if(key1.equals("00")){
            lastMsg = "查询失败，请稍后再试！";
        }
        else{
            lastMsg = CodeToError(key1);
        }
        RESULT ret = RESULT.ERROR;
        return ret;
    }
    public RESULT queryDkTj(String date){
        String url = host;
        url += "/SbsqWW/dktj.do?callback=jQuery";
        url += GetTickCount();

        String tjyf=date;
        tjyf = tjyf.replaceAll("-+","");
        tjyf = tjyf.substring(0,6);

        Map<String,String> params = new HashMap<>();
        params.put("cert",taxNo);
        params.put("token",token);
        params.put("ymbb",ymbb);
        params.put("oper","tj");
        params.put("tjyf",tjyf);
        String response = Request(url,params,15000);
        if(response==null||response.isEmpty()){
            lastMsg = _EMPTY;
            return RESULT.ERROR;
        }
        rpJson = TakeJson(response);
        String key1="";
        try{
            JSONObject object = JSON.parseObject(rpJson);
            if(object.get("key1")==null || object.getString("key1").isEmpty()){
                lastMsg = "抵扣统计查询失败";
                return RESULT.ERROR;
            }
            key1 = object.getString("key1");
            if(key1.equals("20")){
                lastMsg=_SUCCESS;
                token = object.getString("key3");
                rpJson = object.getString("key2");
                return RESULT.SUCCESS;
            }
        }
        catch(JSONException e){
            lastMsg = "queryDkTj json parse error";
            return RESULT.ERROR;
        }
        RESULT ret = RESULT.ERROR;
        if(key1.equals("50")){
            lastMsg = "抵扣统计查询失败";
        }
        else if(key1.equals("22")){
            lastMsg="缺少统计信息";
        }
        else{
            lastMsg = CodeToError(key1);
        }
        return ret;
    }
    public RESULT queryQrHz(String ssq){
        String key1="";
        String url = host;
        url += "/SbsqWW/qrgx.do?callback=jQuery";
        url += GetTickCount();

        String cxSsq = ssq.replaceAll("-*","");
        cxSsq = cxSsq.substring(0,6);

        Map<String,String> params = new HashMap<>();
        params.put("id","querylsqrxx");
        params.put("ssq",cxSsq);
        params.put("key1",taxNo);
        params.put("key2",token);
        params.put("ymbb",ymbb);
        String response = Request(url,params,15000);
        if(response==null||response.isEmpty()){
            lastMsg = _EMPTY;
            return RESULT.ERROR;
        }
        rpJson = TakeJson(response);
        try{
            JSONObject root = JSON.parseObject(rpJson);
            key1 = root.getString("key1");
            if(key1.equals("01")){
                token = root.getString("key3");
                rpJson = root.getString("key2");
                int bgnPos = rpJson.indexOf('"');
                int endPos = rpJson.lastIndexOf('"');
                if(bgnPos!=-1 && endPos!=-1){
                    rpJson = rpJson.substring(bgnPos+1,endPos-bgnPos-1);
                }
                lastMsg = _SUCCESS;
                return RESULT.SUCCESS;
            }
        }
        catch(JSONException e){
            lastMsg = "queryQrHz json error";
            return RESULT.ERROR;
        }
        if(key1.equals("00")){
            lastMsg = "查询发票信息出现异常，请稍后再试！";
        }
        else{
            lastMsg = CodeToError(key1);
        }
        RESULT ret = RESULT.ERROR;
        return ret;
    }
    public RESULT queryFromGx(Query.Fp query){
        String aoData = BuildPagerJson(query,14);
        String url = host;
        url += "/SbsqWW/gxcx.do?callback=jQuery";
        url += GetTickCount();
        Map<String,String> params = new HashMap<>();
        params.put("fpdm","");
        params.put("fphm","");
        params.put("xfsbh","");
        if(query.rz.equals("yes")){
            params.put("rzzt","1");
            params.put("rzfs","-1");
        }
        else{
            params.put("rzzt","0");
            params.put("rzfs","");
        }
        params.put("gxzt","-1");
        params.put("fpzt","-1");
        params.put("fplx","-1");
        params.put("cert",taxNo);
        params.put("rq_q",query.ksrq);
        params.put("rq_z",query.jsrq);
        params.put("aoData",aoData);
        params.put("ymbb",ymbb);
        params.put("token",token);
        String response = Request(url,params,15000);
        if(response==null||response.isEmpty()){
            lastMsg = _EMPTY;
            return RESULT.ERROR;
        }
        rpJson = TakeJson(response);
        String key1="";
        try{
            JSONObject jsonObject = JSON.parseObject(rpJson);
            key1 = jsonObject.getString("key1");
            if(key1.equals("01")){
                if(jsonObject.getString("key4").equals("0")){
                    lastMsg = "没有符合条件的记录";
                    return RESULT.ERROR;
                }
                lastMsg = _SUCCESS;
                token = jsonObject.getString("key3");
                rpJson= jsonObject.getString("key2");
                return RESULT.SUCCESS;
            }
            if(key1.equals("00")){
               lastMsg = jsonObject.getString("key2");
               return RESULT.ERROR;
            }
        }
        catch(Exception e){
            e.printStackTrace();
            lastMsg = "mainCollectByYear json error";
            return RESULT.ERROR;
        }
        lastMsg = CodeToError(key1);
        return RESULT.ERROR;
    }
    private RESULT queryQrgx(){
        String url = host;
        url += "/SbsqWW/qrgx.do?callback=jQuery";
        url += GetTickCount();

        Map<String,String> params = new HashMap<>();
        params.put("id","queryqrxx");
        params.put("key1",taxNo);
        params.put("key2",token);
        params.put("ymbb",ymbb);
        String response = Request(url,params,15000);
        if(response==null||response.isEmpty()){
            lastMsg = _EMPTY;
            return RESULT.ERROR;
        }
        rpJson = TakeJson(response);
        String key1="";
        try{
            JSONObject root = JSON.parseObject(rpJson);
            key1 = root.getString("key1");
            if(key1.equals("01")){
                data.ljrzs = root.getString("key2");
                data.dqrq  = root.getString("key5");
                token  = root.getString("key3");
                return RESULT.SUCCESS;
            }
        }
        catch(JSONException e){
            lastMsg = "queryQrgx json error";
            return RESULT.ERROR;
        }
        if(key1.equals("00")){
            lastMsg = "查询您当前税款所属期的已确认信息出现异常，请稍后再试";
        }
        else{
            lastMsg = CodeToError(key1);
        }
        return RESULT.ERROR;
    }
    private RESULT beforeConfirmGx(){
        String url = host;
        url += "/SbsqWW/hqssq.do?callback=jQuery";
        url += GetTickCount();

        Map<String,String> params = new HashMap<>();
        params.put("cert",taxNo);
        params.put("token",token);
        params.put("ymbb",ymbb);
        String response = Request(url,params,15000);
        if(response==null||response.isEmpty()){
            lastMsg = _EMPTY;
            return  RESULT.ERROR;
        }
        rpJson = TakeJson(response);
        String key1="";
        try {
            JSONObject root = JSON.parseObject(rpJson);
            key1 = root.getString("key1");
            if(key1.equals("01")){
                this.data.cookssq = root.getString("key3");
                this.data.gxrqfw = root.getString("key4");
                token = root.getString("key2");
                lastMsg = _SUCCESS;
                return RESULT.SUCCESS;
            }
        }
        catch(JSONException e){
            lastMsg = "beforeConfirmGx json error";
            return RESULT.ERROR;
        }
        RESULT ret = RESULT.ERROR;
        lastMsg = CodeToError(key1);
        return ret;
    }
    private RESULT retryLogin(String authCode,String random,String publickey){
        String url = host;
        url += "/SbsqWW/login.do?callback=jQuery";
        url += GetTickCount();

        Map<String,String> params = new HashMap<>();
        params.put("type","CLIENT-AUTH");
        params.put("clientAuthCode",authCode);
        params.put("serverRandom",random);
        params.put("password","");
        params.put("ts", this.secret.ts);
        params.put("publickey",publickey);
        params.put("cert",taxNo);
        params.put("ymbb",ymbb);

        String response = Request(url,params,15000);
        if(response==null||response.isEmpty()){
            lastMsg = _EMPTY;
            return RESULT.ERROR;
        }
        rpJson = TakeJson(response);
        RESULT ret;
        String key1="";
        try{
            JSONObject root = JSON.parseObject(rpJson);
            key1 = root.getString("key1");
            if(key1.equals("00")){
                ret = RESULT.ERROR;
                lastMsg = "登录失败";
                return ret;
            }
            if(key1.equals("03")){
                lastMsg = _SUCCESS;
                token = root.getString("key2");
                data.nsrmc = HttpUtils.decode(root.getString("key3"),"UTF-8");
                data.dqrq = root.getString("key4");
                return RESULT.SUCCESS;
            }
            if(key1.equals("02")){
                lastMsg = "无此用户";
                return RESULT.ERROR;
            }
        }
        catch(JSONException e){
            e.printStackTrace();
            lastMsg = "ThirdLogin json error";
            return RESULT.ERROR;
        }
        lastMsg = CodeToError(key1);
        ret = RESULT.ERROR;
        return ret;
    }
    private RESULT querySecret(String funType){
        String url = host;
        url += "/SbsqWW/querymm.do?callback=jQuery";
        url += GetTickCount();

        Map<String,String> params = new HashMap<>();
        params.put("cert",taxNo);
        params.put("funType",funType);
        String response = Request(url,params,15000);
        if(response==null||response.isEmpty()){
            lastMsg = _EMPTY;
            return RESULT.ERROR;
        }
        rpJson = TakeJson(response);
        try {
            JSONObject root = JSON.parseObject(rpJson);
            secret.page  = root.getString("page");
            secret.ts = root.getString("ts");
            lastMsg = _SUCCESS;
        }
        catch(JSONException e){
            e.printStackTrace();
            lastMsg = "SecondLogin json error";
            return RESULT.ERROR;
        }
        return RESULT.SUCCESS;
    }
    boolean isOvertime(){
        return lastMsg.equals(_EMPTY);
    }
    public String GetReponseJson(){
        return rpJson;
    }
    public String getLastMsg(){
        if(isOvertime())
            return "请求第三方访问超时";
        return lastMsg;
    }
    public void setYmbb(String ymbb){
        this.ymbb = ymbb;
    }
    public String getToken(){
        return token;
    }
    public void setToken(String token){
        this.token = token;
    }
    public Data getData(){
        return this.data;
    }
    public void setData(Data data){
        this.data =  data;
    }
    public void setHost(String host){
        this.host = host;
    }
    public void setTaxNo(String taxNo){
        this.taxNo = taxNo;
    }
    public String getRpJson(){
        return rpJson;
    }

    public Secret getSecret() {
        return secret;
    }

}

