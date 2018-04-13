package GxPtProxy.Controller;

import Base.Result;
import Base.ResultFactory;
import GxPtProxy.*;
import GxPtProxy.Bean.*;
import GxPtProxy.Validator.LoginValidator;
import GxPtProxy.Validator.ParamValidator;
import GxPtProxy.Validator.UsualValidator;
import org.apache.catalina.Session;
import org.apache.catalina.connector.RequestFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;

@RestController
@RequestMapping(value="/Gxpt",produces="text/plain;charset=UTF-8")
public class GxptProxy {
    @Autowired
    private GxptArea gxptArea;  //勾选平台地区
    @Autowired
    private StartOnLoad startOnLoad;
    @Autowired
    private JsEngine jsEngine;

    @Value("${gxpt.ymbb}")
    private String ymbb;
    /*
    @RequestMapping("/Test")
    Object test(){
        return ResultFactory.Failure("test","something");
    }
    */

    /*
        首次登录
        taxNo 税号
        area  地区拼音
        hello 税控盘证书xxx
    */
    @RequestMapping("/Login")
    @ParamValidator(validatorClass = LoginValidator.class)
    Object login(String taxNo,String area,String hello,HttpServletRequest httpServletRequest){
        String host="";
        try{
            Field field = GxptArea.class.getDeclaredField(area);
            field.setAccessible(true);
            host = (String) field.get(gxptArea);
        }catch (Exception e){

        }
        //如果已登录，先退出
        User user = SessionManager.getUser(httpServletRequest);
        ChangR changr = new ChangR();
        changr.setYmbb(ymbb);
        changr.setHost(host);
        if(!user.getTaxNo().isEmpty()) {
            changr.Quit();  //先退出
        }
        ChangR.RESULT result = changr.Login(hello);
        if(result==ChangR.RESULT.ERROR) {
            return ResultFactory.Failure("-1",changr.getLastMsg());
        }
        //存储用户信息至session
        user.setHost(host);
        user.setTaxNo(taxNo);
        user.setToken(changr.getToken());
        SessionManager.addSession(user,httpServletRequest);
        //返回首次登录数据
        Object respone;
        if(result==ChangR.RESULT.SUCCESS){
            LoginDone loginDone = new LoginDone();
            loginDone.setDqrq(changr.getData().dqrq);
            loginDone.setNsrmc(changr.getData().nsrmc);
            loginDone.setToken(changr.getToken());
            respone = loginDone;
        }
        else{
            NextLogin firstLogin = new NextLogin();
            firstLogin.setRandom( changr.getData().random );
            firstLogin.setPacket( changr.getData().packet );
            respone = firstLogin;
        }
        return ResultFactory.Success(respone);
    }
    /*
        authCode 首次登录的验证码
        random   首次登录的随机数
        taxNo    税号
    * */
    @RequestMapping("/SecondLogin")
    @ParamValidator(validatorClass = UsualValidator.class)
    Object secondLogin(String authCode,String taxNo,String random,HttpServletRequest httpServletRequest) {
        User user = SessionManager.getUser(httpServletRequest);
        ChangR changr = allocByUser(user);
        String publickey="";
        ChangR.RESULT result = changr.SecondLogin(authCode,random);
        if(ChangR.RESULT.NEXT_LOGIN==result){
            publickey = jsEngine.checkTaxno(taxNo,changr.getData().ts,"",changr.getData().page,random);
            int nLoop = 0;
            do{
                result = changr.ThirdLogin(authCode,random,publickey);
                if(result==ChangR.RESULT.SUCCESS) break;
                nLoop+=1;
            }while(nLoop<3);
        }
        if(result==ChangR.RESULT.SUCCESS) {
            //更新token等信息
            user.setTaxNo(taxNo);
            user.setToken(changr.getToken());
            SessionManager.addSession(user,httpServletRequest);
            //构造返回结果
            LoginDone loginDone = new LoginDone();
            loginDone.setDqrq( changr.getData().dqrq );
            loginDone.setNsrmc( changr.getData().nsrmc );
            loginDone.setToken( changr.getToken() );
            return ResultFactory.Success(loginDone);
        }
        return ResultFactory.Failure("-1",changr.getLastMsg());
    }
    /*
    *   按年份查询首页统计数据
    *   taxNo 税号
    *   year  查询的年份
    * */
    @RequestMapping("/mainCollectByYear")
    @ParamValidator(validatorClass = UsualValidator.class)
    Object mainCollectByYear(String taxNo,String year,String token,HttpServletRequest request){
        User user = SessionManager.getUser(request);
        user.setToken(token);

        ChangR changr = allocByUser(user);
        ChangR.RESULT result = changr.mainCollectByYear(year);
        if(result!=ChangR.RESULT.SUCCESS) {
            return ResultFactory.Failure("-1",changr.getLastMsg());
        }
        //解析数据
        String rpJson = changr.getRpJson();
        MainCollect mainCollect = Parser.manCollect(rpJson);
        mainCollect.setToken(changr.getToken());
        return ResultFactory.Success( mainCollect );
    }
    /*
    *
    *  从发票勾选页面查询发票数据（可能是当前所属期的可勾选发票和已认证发票）
    *  taxNo 税号
    *  分页参数 page 页面索引 max 单页数据量
    *  rz 认证状态 YES,NO
    *  ksrq 开始日期
    *  jsrq 结束日期
    * */
    @RequestMapping("/queryFromGx")
    @ParamValidator(validatorClass = UsualValidator.class)
    Object queryFromGx(String taxNo,String token,String page,String max,String rz,String ksrq,String jsrq,HttpServletRequest request){
        User user = SessionManager.getUser(request);
        user.setToken(token);

        ChangR changr = allocByUser(user);
        ChangR.RESULT result = changr.queryFromGx( new Query.Fp(page,max,rz,ksrq,jsrq) );
        if(result==ChangR.RESULT.ERROR){
            return ResultFactory.Failure("-1",changr.getLastMsg());
        }
        user.setToken( changr.getToken() );
        SessionManager.addSession(user,request);

        QueryFpDone queryFpDone = new QueryFpDone();
        queryFpDone.setInvoices( Parser.fromGx(changr.getRpJson()) );
        queryFpDone.setToken( changr.getToken() );
        return ResultFactory.Success(queryFpDone);
    }
    /*
    *  抵扣统计数据查询
    *  taxNo 税号
    *  date 年月日 201702
    * */
    @RequestMapping("/queryDkTj")
    @ParamValidator(validatorClass = UsualValidator.class)
    Object queryDkTj(String taxNo,String token,String date,HttpServletRequest request){
        User user = SessionManager.getUser(request);
        user.setToken(token);

        ChangR changr = allocByUser(user);
        ChangR.RESULT result = changr.queryDkTj(date);
        if(result==ChangR.RESULT.ERROR){
            return ResultFactory.Failure("-1",changr.getLastMsg());
        }
        user.setToken(changr.getToken());
        SessionManager.addSession(user,request);
        DkTjDone dkTjDone = new DkTjDone();
        dkTjDone.setToken(changr.getToken());
        dkTjDone.setDkTjList( Parser.DkTj(changr.getRpJson()) );
        return ResultFactory.Success(dkTjDone);
    }

    /*
    *  按税款所属期查询已认证数据
    *  taxNo 税号
    *  date 年月日 2017-02-01
    *  目前该接口数据没有使用
    * */
    @RequestMapping("/queryFromQrgx")
    @ParamValidator(validatorClass = UsualValidator.class)
    Object queryFromQrgx(String taxNo,String token,String date,HttpServletRequest request){
        User user = SessionManager.getUser(request);
        user.setToken(token);

        ChangR changr = allocByUser(user);
        ChangR.RESULT result = changr.queryFromQrgx(date);
        if(result==ChangR.RESULT.ERROR){
            return ResultFactory.Failure("-1",changr.getLastMsg());
        }
        user.setToken(changr.getToken());
        SessionManager.addSession(user,request);
        return ResultFactory.Success();
    }
    /*
    * 查询勾选认证发票
    * 查询条件
    * gxrz  GX_N_QR 已勾选未认证 GX_QR 已勾选已认证
    *
    * */
    @RequestMapping("/queryFromGxRz")
    @ParamValidator(validatorClass = UsualValidator.class)
    Object queryFromGxRz(String taxNo,String token,String gxrz,String page,String max,HttpServletRequest request){
        User user = SessionManager.getUser(request);
        user.setToken(token);

        ChangR changr = allocByUser(user);
        ChangR.RESULT result = changr.queryFromGxRz(new Query.GxRz(page,max,gxrz));
        if(result==ChangR.RESULT.ERROR){
            return ResultFactory.Failure("-1",changr.getLastMsg());
        }
        user.setToken(changr.getToken());
        SessionManager.addSession(user,request);

        QueryFpDone queryFpDone = new QueryFpDone();
        queryFpDone.setInvoices( Parser.fromGx(changr.getRpJson()) );
        queryFpDone.setToken( changr.getToken() );
        return ResultFactory.Success(queryFpDone);
    }
    /*
     * 查询抵扣数据
     * 查询条件
     * tjyf 统计月份 201802
     * xfsbh 销方纳税人识别号
     * qrrzrq_q 确认认证日期起
     * qrrzrq_z 确认认证日期止
     * */
    @RequestMapping("/queryDk")
    @ParamValidator(validatorClass = UsualValidator.class)
    Object queryDk(String taxNo,String token,String page,String max,String tjyf,String xfsbh,
                   String qrrzrq_q,String qrrzrq_z, HttpServletRequest request){
        User user = SessionManager.getUser(request);
        user.setToken(token);

        ChangR changr = allocByUser(user);
        ChangR.RESULT result = changr.queryDk(new Query.Dkcx(page,max,tjyf,xfsbh,qrrzrq_q,qrrzrq_z));
        if(result==ChangR.RESULT.ERROR){
            return ResultFactory.Failure("-1",changr.getLastMsg());
        }
        user.setToken( changr.getToken() );
        SessionManager.addSession(user,request);

        QueryFpDone queryFpDone = new QueryFpDone();
        queryFpDone.setToken(changr.getToken());
        queryFpDone.setInvoices( Parser.fromDk(changr.getRpJson()) );
        return ResultFactory.Success(queryFpDone);
    }

    protected ChangR allocByUser(User user){
        ChangR changr = new ChangR();
        changr.setYmbb(ymbb);
        changr.setHost(user.getHost());
        changr.setToken(user.getToken());
        changr.setTaxNo(user.getTaxNo());
        return changr;
    }
}
