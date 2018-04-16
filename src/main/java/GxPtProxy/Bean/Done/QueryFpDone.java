package GxPtProxy.Bean.Done;

import GxPtProxy.Bean.Invoice;

import java.util.ArrayList;
import java.util.List;

public class QueryFpDone extends Done {
    private List<Invoice> invoices = new ArrayList<>();
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
}
