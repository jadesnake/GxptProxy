package GxPtProxy;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.*;

@Component
public class JsEngine {
    protected static final Logger logger = LoggerFactory.getLogger(ChangR.class);

    private ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    private ScriptEngine nashorn;
    public JsEngine(){
        nashorn = scriptEngineManager.getEngineByName("nashorn");
        init();
    }
    public void init(){
        try {
            File file = ResourceUtils.getFile("classpath:encrypt.js");
            nashorn.eval( new FileReader(file) );
            //nashorn.eval(js);
            //直接读取数据出现js错误
        }
        catch(ScriptException e) {
            int eLine = e.getLineNumber();
            int eColumn = e.getColumnNumber();
            String eMsg = e.getMessage();
            e.printStackTrace();
        }
        catch(FileNotFoundException fileE){
            fileE.printStackTrace();;
        }
    }
    public String checkTaxno(String a,String b,String c,String d,String e){
        String ret="";
        Invocable invocable = (Invocable)nashorn;
        Object result = null;
       try{
           Object scope  = nashorn.get("jQueryT");
           result = invocable.invokeMethod(scope, "checkTaxno",a,b,c,d,e);
           ret = (String) result;
        }
        catch(NoSuchMethodException mehtodE){
            mehtodE.printStackTrace();
        }
        catch (ScriptException scpritE){
            scpritE.printStackTrace();
        }
        return ret;
    }
    public String checkInvConf(String a,String b,String c,String d,String e){
        String ret="";
        Invocable invocable = (Invocable)nashorn;
        Object result = null;
        try{
            Object scope  = nashorn.get("jQueryT");
            result = invocable.invokeMethod(scope, "checkInvConf",a,b,c,d,e);
            ret = (String) result;
        }
        catch(NoSuchMethodException mehtodE){
            mehtodE.printStackTrace();
        }
        catch (ScriptException scpritE){
            scpritE.printStackTrace();
        }
        return ret;
    }
}
