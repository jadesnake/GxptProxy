package GxPtProxy.Bean;

import GxPtProxy.Bean.Done.Done;

import java.util.ArrayList;
import java.util.List;

public class DkTj extends Done {
    private String label="";    //标签
    private List<Statistics> groupList = new ArrayList<>();

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Statistics> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Statistics> groupList) {
        this.groupList = groupList;
    }
    public void addGroup(Statistics tj){
        this.groupList.add(tj);
    }
}
