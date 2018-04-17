package GxPtProxy.Controller;

import Base.ResultFactory;
import GxPtProxy.*;
import GxPtProxy.Bean.*;
import GxPtProxy.Bean.Done.*;
import GxPtProxy.Bean.Request.Gx;
import GxPtProxy.Gxpt.ChangR;
import GxPtProxy.Gxpt.GxptArea;
import GxPtProxy.Gxpt.Parser;
import GxPtProxy.Validator.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.List;

@RestController
@RequestMapping(value="/Gxpt",produces="text/plain;charset=UTF-8")
public class GxptProxy {
    private static final Logger logger = LoggerFactory.getLogger(GxptProxy.class);

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
    @RequestMapping("/Login.do")
    @ParamValidator(validatorClass = LoginValidator.class)
    public Object login(String taxNo,String area,String hello,HttpServletRequest httpServletRequest){
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
            changr.quit();  //先退出
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
    @RequestMapping("/SecondLogin.do")
    @ParamValidator(validatorClass = UsualValidator.class)
    public Object secondLogin(String authCode,String taxNo,String random,HttpServletRequest httpServletRequest) {
        User user = SessionManager.getUser(httpServletRequest);
        ChangR changr = allocByUser(user);
        String publickey="";
        ChangR.RESULT result = changr.SecondLogin(authCode, random, new MakeSecret() {
            @Override
            public String checkTaxno(String a, String b, String c, String d, String e) {
                return jsEngine.checkTaxno(a,b,c,d,e);
            }
        });
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
    @RequestMapping("/mainCollectByYear.do")
    @ParamValidator(validatorClass = UsualValidator.class)
    public Object mainCollectByYear(String taxNo,String year,String token,HttpServletRequest request){
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
    @RequestMapping("/queryFromGx.do")
    @ParamValidator(validatorClass = UsualValidator.class)
    public Object queryFromGx(String taxNo,String token,String page,String max,String rz,String ksrq,String jsrq,HttpServletRequest request){
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
    @RequestMapping("/queryDkTj.do")
    @ParamValidator(validatorClass = UsualValidator.class)
    public Object queryDkTj(String taxNo,String token,String date,HttpServletRequest request){
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
        dkTjDone.setTjList( Parser.DkTj(changr.getRpJson()) );
        return ResultFactory.Success(dkTjDone);
    }

    /*
    *  按税款所属期查询已认证数据
    *  taxNo 税号
    *  date 年月日 2017-02-01
    *  目前该接口数据没有使用
    * */
    @RequestMapping("/queryFromQrgx.do")
    @ParamValidator(validatorClass = UsualValidator.class)
    public Object queryFromQrgx(String taxNo,String token,String date,HttpServletRequest request){
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
    @RequestMapping("/queryFromGxRz.do")
    @ParamValidator(validatorClass = UsualValidator.class)
    public Object queryFromGxRz(String taxNo,String token,String gxrz,String page,String max,HttpServletRequest request){
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
    @RequestMapping("/queryDk.do")
    @ParamValidator(validatorClass = DkValidator.class)
    public Object queryDk(String taxNo,String token,String page,String max,String tjyf,String xfsbh,
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
    /*
    * 查询 确认发票汇总 数据
    * 查询条件
    * ssq 所属期
    *
    * */
    @RequestMapping("/queryQrHz.do")
    @ParamValidator(validatorClass = UsualValidator.class)
    public Object queryQrHz(String taxNo,String token,String ssq,HttpServletRequest request){
        User user = SessionManager.getUser(request);
        user.setToken(token);

        ChangR changr = allocByUser(user);
        ChangR.RESULT result = changr.queryQrHz(ssq);
        if(result==ChangR.RESULT.ERROR){
            return ResultFactory.Failure("-1",changr.getLastMsg());
        }
        user.setToken( changr.getToken() );
        SessionManager.addSession(user,request);

        List<QrHz> qrHzsList = Parser.fromQrHz(changr.getRpJson());
        QrHzDone done = new QrHzDone();
        done.setQrHzList(qrHzsList);
        done.setToken(changr.getToken());
        return ResultFactory.Success(done);
    }
    /*
     *   获取企业信息
     *
     *
     */
    @RequestMapping("/queryInfo.do")
    @ParamValidator(validatorClass = UsualValidator.class)
    public Object queryInfo(String taxNo,String token,HttpServletRequest request){
        User user = SessionManager.getUser(request);
        user.setToken(token);
        ChangR changr = allocByUser(user);
        ChangR.RESULT result = changr.queryInfo();
        if(result==ChangR.RESULT.ERROR){
            return ResultFactory.Failure("-1",changr.getLastMsg());
        }
        user.setToken( changr.getToken() );
        SessionManager.addSession(user,request);

        UserInfoDone done = new UserInfoDone();
        done.setToken(changr.getToken());
        done.setUserInfo(Parser.fromQueryQy(changr.getRpJson(),changr.getToken()));
        return ResultFactory.Success(done);
    }
    /*
     *  保存勾选状态
     *
     *
     * */
    @RequestMapping("/submitGx.do")
    @ParamValidator(validatorClass = SubmitGxValidator.class)
    public Object submitGx(@RequestBody Gx rqParam, HttpServletRequest request){
        User user = SessionManager.getUser(request);
        user.setToken(rqParam.getToken());

        ChangR changr = allocByUser(user);
        ChangR.RESULT result = changr.submitGx(rqParam, new MakeSecret() {
            @Override
            public String checkInvConf(String a, String b, String c, String d, String e) {
                return jsEngine.checkInvConf(a,b,c,d,e);
            }
        });
        if(result==ChangR.RESULT.ERROR){
            return ResultFactory.Failure("-1",changr.getLastMsg());
        }
        user.setToken(changr.getToken());
        SessionManager.addSession(user,request);
        return  ResultFactory.Success();
    }
    /*
    * 对已经保存勾选的数据预认证
    * 查询条件
    * taxNo 税号
    * token 令牌
    * nsrmc 纳税人名称
    * */
    @RequestMapping("/startConfirmGx.do")
    @ParamValidator(validatorClass = UsualValidator.class)
    public Object startConfirmGx(String taxNo,String token,String nsrmc,HttpServletRequest request){
        User user = SessionManager.getUser(request);
        user.setToken(token);
        ChangR changr = allocByUser(user);
        ChangR.RESULT result = changr.startConfirmGx(nsrmc);
        if(result==ChangR.RESULT.ERROR){
            return ResultFactory.Failure("-1",changr.getLastMsg());
        }
        user.setToken( changr.getToken() );
        SessionManager.addSession(user,request);
        RzHz cur=null,dq=null;
        if(!Parser.startConfirm(changr.getRpJson(),cur,dq)){
            return ResultFactory.Failure("-1","数据解析异常");
        }
        RzHzDone rzHzDone = new RzHzDone();
        rzHzDone.setCur(cur);
        rzHzDone.setDq(dq);
        rzHzDone.setLjhzxxfs( changr.getData().ljhzxxfs );
        rzHzDone.setSignature( changr.getData().signature );
        rzHzDone.setToken(changr.getToken());
        return ResultFactory.Success(rzHzDone);
    }
    /*
     * 对已经保存勾选的数据认证
     * 查询条件
     * taxNo 税号
     * token 令牌
     * nsrmc 纳税人名称
     * */
    @RequestMapping("/endConfirmGx.do")
    @ParamValidator(validatorClass = UsualValidator.class)
    public Object endConfirmGx(String taxNo,String token,String ljhzxxfs,String signature,HttpServletRequest request){
        User user = SessionManager.getUser(request);
        user.setToken(token);
        ChangR changr = allocByUser(user);
        ChangR.RESULT result = changr.endConfirmGx(ljhzxxfs,signature);
        if(result==ChangR.RESULT.ERROR){
            return ResultFactory.Failure("-1",changr.getLastMsg());
        }
        user.setToken( changr.getToken() );
        SessionManager.addSession(user,request);
        return ResultFactory.Success();
    }
    /*
    *   退出系统
    *   quit
    *
    * */
    @RequestMapping("/quit.do")
    @ParamValidator(validatorClass = UsualValidator.class)
    public Object quit(String taxNo,HttpServletRequest request ){
        User user = SessionManager.getUser(request);
        ChangR changr = allocByUser(user);
        ChangR.RESULT result = changr.quit();
        if(result==ChangR.RESULT.ERROR){
            return ResultFactory.Failure("-1",changr.getLastMsg());
        }
        SessionManager.removeSession(user,request);
        return ResultFactory.Success();
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
