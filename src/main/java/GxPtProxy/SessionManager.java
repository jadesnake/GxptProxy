package GxPtProxy;

import GxPtProxy.Bean.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

public class SessionManager {
    private static String LOGIN_USER_KEY = "login.user.key";

    private static  void clearSession(HttpServletRequest httpServletRequest){
        try {
            HttpSession session = httpServletRequest.getSession();
            Enumeration names = session.getAttributeNames();
            while (names.hasMoreElements()) {
                session.removeAttribute(names.nextElement().toString());
            }
        } catch (Exception e) {
        }
    }
    public static User getUser(HttpServletRequest httpServletRequest){
        HttpSession session = httpServletRequest.getSession();
        Object obj = session.getAttribute(LOGIN_USER_KEY);
        if(obj!=null && obj instanceof User){
            return (User)obj;
        }
        return new User();
    }
    public static void addSession(User user, HttpServletRequest httpServletRequest){
        HttpSession httpSession = httpServletRequest.getSession();
        Object obj = httpSession.getAttribute(LOGIN_USER_KEY);
        if(obj!=null)
            clearSession(httpServletRequest);
        httpSession.setAttribute(LOGIN_USER_KEY, user);
        httpSession.setAttribute("_TAXNO",user.getTaxNo());
        httpSession.setAttribute("_TOKEN",user.getToken());
        httpSession.setAttribute("_HOST",user.getHost());
    }
}
