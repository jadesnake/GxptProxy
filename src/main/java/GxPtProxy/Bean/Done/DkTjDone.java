package GxPtProxy.Bean.Done;

import GxPtProxy.Bean.DkTj;

import java.util.ArrayList;
import java.util.List;

public class DkTjDone extends Done{
    private List<DkTj> tjList = new ArrayList<>();

    public List<DkTj> getTjList() {
        return tjList;
    }
    public void setTjList(List<DkTj> tjList) {
        this.tjList = tjList;
    }
}
