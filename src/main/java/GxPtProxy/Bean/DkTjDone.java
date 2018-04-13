package GxPtProxy.Bean;

import java.util.ArrayList;
import java.util.List;

public class DkTjDone {
    private String token="";
    private List<DkTj> dkTjList = new ArrayList<>();

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<DkTj> getDkTjList() {
        return dkTjList;
    }

    public void setDkTjList(List<DkTj> dkTjList) {
        this.dkTjList = dkTjList;
    }
}
