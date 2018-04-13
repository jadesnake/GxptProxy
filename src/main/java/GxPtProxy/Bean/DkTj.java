package GxPtProxy.Bean;

import java.util.ArrayList;
import java.util.List;

public class DkTj {
    public static class Group
    {
        public String label=""; //标签
        public String sl="";    //数量
        public String se="";    //税额
        public String je="";    //金额
    };
    private String label="";    //标签
    private List<Group> groupList = new ArrayList<>();

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }
    public void addGroup(Group group){
        this.groupList.add(group);
    }
}
