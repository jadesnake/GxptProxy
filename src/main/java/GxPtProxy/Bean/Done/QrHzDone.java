package GxPtProxy.Bean.Done;

import GxPtProxy.Bean.QrHz;

import java.util.ArrayList;
import java.util.List;

public class QrHzDone extends Done{
    private List<QrHz> qrHzList = new ArrayList<>();

    public List<QrHz> getQrHzList() {
        return qrHzList;
    }

    public void setQrHzList(List<QrHz> qrHzList) {
        this.qrHzList = qrHzList;
    }
}
