
package Base;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.google.common.base.Strings;
import org.slf4j.LoggerFactory;

/**
 * https 请求 微信为https的请求
 * 
 */
public class HttpUtils {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static final String DEFAULT_CHARSET = "UTF-8"; // 默认字符集
    private static final String _GET            = "GET";   // GET
    private static final String _POST           = "POST";  // POST
    private static final int _CONNECT_TIMEOUT = 25000;   //链接超时
    /**
     * 初始化http请求参数
     * 
     * @param url
     * @param method
     * @param headers
     * @return
     * @throws IOException
     */
    private static HttpURLConnection initHttp(String url, String method,
                                              Map<String, String> headers,String cookie) throws IOException {
        URL _url = new URL(url);
        HttpURLConnection http = (HttpURLConnection) _url.openConnection();
        // 连接超时
        http.setConnectTimeout(25000);
        // 读取超时 --服务器响应比较慢，增大时间
        http.setReadTimeout(25000);
        http.setRequestMethod(method);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        http.setRequestProperty("User-Agent",
            "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        if (null != headers && !headers.isEmpty()) {
            for (Entry<String, String> entry : headers.entrySet()) {
                http.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        if(cookie!=null && !cookie.isEmpty()){
            http.setRequestProperty("Cookie",cookie);
        }
        http.setDoOutput(true);
        http.setDoInput(true);
        http.connect();
        return http;
    }

    /**
     * 初始化http请求参数
     * 
     * @param url
     * @param method
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws KeyManagementException
     */
    private static HttpsURLConnection initHttps(String url, String method,
                                                Map<String, String> headers,String cookie) throws IOException,
                                                                             NoSuchAlgorithmException,
                                                                             NoSuchProviderException,
                                                                             KeyManagementException {
        TrustManager[] tm = { new MyX509TrustManager() };
        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
        sslContext.init(null, tm, new java.security.SecureRandom());
        // 从上述SSLContext对象中得到SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        URL _url = new URL(url);
        HttpsURLConnection http = (HttpsURLConnection) _url.openConnection();
        // 设置域名校验
        http.setHostnameVerifier(new HttpUtils().new TrustAnyHostnameVerifier());
        // 连接超时
        http.setConnectTimeout(25000);
        // 读取超时 --服务器响应比较慢，增大时间
        http.setReadTimeout(25000);
        http.setRequestMethod(method);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        http.setRequestProperty("User-Agent",
            "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        if (null != headers && !headers.isEmpty()) {
            for (Entry<String, String> entry : headers.entrySet()) {
                http.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        if(cookie!=null && !cookie.isEmpty()){
            http.setRequestProperty("Cookie",cookie);
        }
        http.setSSLSocketFactory(ssf);
        http.setDoOutput(true);
        http.setDoInput(true);
        http.connect();
        return http;
    }

    /**
     * 
     * @description 功能描述: get 请求
     * @return 返回类型:
     */
    public static String get(String url, Map<String, String> params, Map<String, String> headers,String cookie) {
        StringBuffer bufferRes = null;
        try {
            HttpURLConnection http = null;
            if (isHttps(url)) {
                http = initHttps(initParams(url, params), _GET, headers,cookie);
            } else {
                http = initHttp(initParams(url, params), _GET, headers,cookie);
            }

            logger.trace("output begin url");
            logger.trace(initParams(url, params));
            logger.trace("output end");

            String charset = DEFAULT_CHARSET;
            Pattern pattern = Pattern.compile("charset=\\S*");
            Matcher matcher = pattern.matcher(http.getContentType());
            if (matcher.find()) {
                charset = matcher.group().replace("charset=", "");
            }
            InputStream in = http.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in, charset));
            String valueString = null;
            bufferRes = new StringBuffer();
            while ((valueString = read.readLine()) != null) {
                bufferRes.append(valueString);
            }
            read.close();
            in.close();
            if (http != null) {
                http.disconnect();// 关闭连接
            }

            logger.trace("input begin");
            logger.trace(bufferRes.toString());
            logger.trace("input end");

            return bufferRes.toString();
        } catch (Exception e) {
            e.printStackTrace();
            logger.trace("request error");
            logger.trace(url);
            logger.trace(e.getMessage());
            return null;
        }
    }

    /**
     * 
     * @description 功能描述: get 请求
     * @return 返回类型:
     */
    public static String get(String url) {
        return get(url, null);
    }

    /**
     * 
     * @description 功能描述: get 请求
     * @return 返回类型:
     * @throws UnsupportedEncodingException
     */
    public static String get(String url, Map<String, String> params) {
        return get(url, params, null,null);
    }

    /**
     * 
     * @description 功能描述: POST 请求
     * @return 返回类型:
     */
    public static String post(String url, String params, Map<String, String> headers,int timeout,String cookie) {
        StringBuffer bufferRes = null;
        try {
            HttpURLConnection http = null;
            if (isHttps(url)) {
                http = initHttps(url, _POST, headers,cookie);
            } else {
                http = initHttp(url, _POST, headers,cookie);
            }
            //
            http.setConnectTimeout(_CONNECT_TIMEOUT);
            http.setReadTimeout(timeout);

            OutputStream out = http.getOutputStream();
            out.write(params.getBytes(DEFAULT_CHARSET));
            out.flush();
            out.close();

            logger.trace("output begin url");
            logger.trace(url);
            logger.trace(params);
            logger.trace("output end");

            String charset = DEFAULT_CHARSET;
            Pattern pattern = Pattern.compile("charset=\\S*");
            Matcher matcher = pattern.matcher(http.getContentType());
            if (matcher.find()) {
                charset = matcher.group().replace("charset=", "");
            }

            InputStream in = http.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in, charset));
            String valueString = null;
            bufferRes = new StringBuffer();
            while ((valueString = read.readLine()) != null) {
                bufferRes.append(valueString);
            }
            read.close();
            in.close();
            if (http != null) {
                http.disconnect();// 关闭连接
            }

            logger.trace("input begin");
            logger.trace(bufferRes.toString());
            logger.trace("input end");

            return bufferRes.toString();
        } catch (Exception e) {
            e.printStackTrace();
            logger.trace("request error");
            logger.trace(url);
            logger.trace(e.getMessage());
            return null;
        }
    }

    /**
     * post map 请求
     * 
     * @param url
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String post(String url,
                              Map<String, String> params,int timeout) throws UnsupportedEncodingException {
        return post(url, map2Url(params), null,timeout,null);
    }

    public static String post(String url,
                              Map<String, String> params,int timeout,String cookie) throws UnsupportedEncodingException {
        return post(url, map2Url(params), null,timeout,cookie);
    }
    /**
     * post map 请求,headers请求头
     * 
     * @param url
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String post(String url, Map<String, String> params,
                              Map<String, String> headers,int timeout) throws UnsupportedEncodingException {
        return post(url, map2Url(params), headers,timeout,null);
    }

    public static String post(String url, Map<String, String> params,
                              Map<String, String> headers,int timeout,String cookie) throws UnsupportedEncodingException {
        return post(url, map2Url(params), headers,timeout,cookie);
    }
    /**
     * 
     * @description 功能描述: 构造请求参数
     * @return 返回类型:
     * @throws UnsupportedEncodingException
     */
    public static String initParams(String url,
                                    Map<String, String> params) throws UnsupportedEncodingException {
        if (null == params || params.isEmpty()) {
            return url;
        }
        StringBuilder sb = new StringBuilder(url);
        if (url.indexOf("?") == -1) {
            sb.append("?");
        }
        sb.append(map2Url(params));
        return sb.toString();
    }
    public static String decode(String val,String enc){
        String ret=val;
        try{
            ret = URLDecoder.decode(val,enc);
        }catch(UnsupportedEncodingException e){
        }
        return ret;
    }
    public static String encode(String val){
        String ret = val;
        try{
            ret = URLEncoder.encode(val,"UTF-8");
        }
        catch(UnsupportedEncodingException e){

        }
        return ret;
    }
    /**
     * map构造url
     * 
     * @description 功能描述:
     * @return 返回类型:
     * @throws UnsupportedEncodingException
     */
    public static String map2Url(Map<String, String> paramToMap) throws UnsupportedEncodingException {
        if (null == paramToMap || paramToMap.isEmpty()) {
            return null;
        }
        StringBuffer url = new StringBuffer();
        boolean isfist = true;
        for (Entry<String, String> entry : paramToMap.entrySet()) {
            if (isfist) {
                isfist = false;
            } else {
                url.append("&");
            }
            url.append(entry.getKey()).append("=");
            String value = entry.getValue();
            if (!Strings.isNullOrEmpty((value))) {
                url.append(URLEncoder.encode(value, DEFAULT_CHARSET));
            }
        }
        return url.toString();
    }

    /**
     * 检测是否https
     * 
     * @param url
     */
    private static boolean isHttps(String url) {
        return url.startsWith("https");
    }

    /**
     * https 域名校验
     *
     * @return
     */
    public class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;// 直接返回true
        }
    }
}

// 证书管理
class MyX509TrustManager implements X509TrustManager {

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    public void checkClientTrusted(X509Certificate[] chain,
                                   String authType) throws CertificateException {
    }

    public void checkServerTrusted(X509Certificate[] chain,
                                   String authType) throws CertificateException {
    }

}