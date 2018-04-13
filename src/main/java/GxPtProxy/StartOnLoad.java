package GxPtProxy;

import Base.OnceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;

@Component
public class StartOnLoad extends OnceLoader {
    private static Logger logger = LoggerFactory.getLogger(StartOnLoad.class);
    private String encryptJs="";
    protected void load(ApplicationContext context){
        File file=null;
        BufferedReader reader=null;
        try{
            file = ResourceUtils.getFile("classpath:encrypt.js");
            reader = new BufferedReader(new FileReader(file));
            StringBuffer jsBuffer = new StringBuffer();
            String jsTmp = null;
            while((jsTmp=reader.readLine())!=null){
                jsBuffer.append(jsTmp);
            }
            encryptJs = jsBuffer.toString();
            reader.close();
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
            return ;
        }
        catch(IOException e){
            e.printStackTrace();
        }
        finally {
            try{
                if(reader!=null)
                    reader.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    public String getEncryptJs(){
        return this.encryptJs;
    }
}
