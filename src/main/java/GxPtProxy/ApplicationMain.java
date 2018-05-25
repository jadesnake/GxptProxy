package GxPtProxy;
import Base.SpringUtil;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import GxPtProxy.ExitException;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.http.converter.HttpMessageConverter;

@SpringBootApplication
public class ApplicationMain implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationMain.class);

    @Bean   //使用@Bean注入fastJsonHttpMessageConvert
    public HttpMessageConverters fastJsonHttpMessageConverters(){
        //1.需要定义一个Convert转换消息的对象
        FastJsonHttpMessageConverter fastConverter=new FastJsonHttpMessageConverter();
        //2.添加fastjson的配置信息，比如是否要格式化返回的json数据
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        //3.在convert中添加配置信息
        fastConverter.setFastJsonConfig(fastJsonConfig);
        HttpMessageConverter<?> converter=fastConverter;
        return new HttpMessageConverters(converter);
    }
    @Bean
    public SpringUtil getSpringUtil(){
        return  new SpringUtil();
    }
    @Override
    public void run(String... args) {
        logger.trace("GxptProxy started");
        if (args.length > 0 && args[0].equals("exitcode")) {
            throw new GxPtProxy.ExitException();
        }
    }
    public static void main(String[] args){
        SpringApplication.run(ApplicationMain.class,args);
    }
}
