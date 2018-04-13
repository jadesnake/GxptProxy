package GxPtProxy.Bean;

import java.util.ArrayList;
import java.util.List;

public class QueryFpDone {
    private List<Invoice> invoices = new ArrayList<>();
    private String token="";
    private String totalRecords="";

    public String getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(String totalRecords) {
        this.totalRecords = totalRecords;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
        this.totalRecords = String.valueOf(invoices.size());
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
