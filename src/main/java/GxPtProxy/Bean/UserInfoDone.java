package GxPtProxy.Bean;

import GxPtProxy.Bean.Done.Done;

public class UserInfoDone extends Done {
    private UserInfo userInfo = new UserInfo();

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
