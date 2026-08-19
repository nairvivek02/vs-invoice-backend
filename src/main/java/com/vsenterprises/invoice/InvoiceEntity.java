package com.vsenterprises.invoice;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "invoices")
public class InvoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNo;
    private String invoiceDate;
    private String poNo;
    private String poDate;

    private String billToName;
    private String billToAddress;
    private String billToGst;
    private String billToState;

    private String shipToDetails;
    private String shipToGst;
    private String shipToState;

    private double totalAmount;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<InvoiceItemEntity> items;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getInvoiceNo() { return invoiceNo; }
    public void setInvoiceNo(String v) { this.invoiceNo = v; }
    public String getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(String v) { this.invoiceDate = v; }
    public String getPoNo() { return poNo; }
    public void setPoNo(String v) { this.poNo = v; }
    public String getPoDate() { return poDate; }
    public void setPoDate(String v) { this.poDate = v; }
    public String getBillToName() { return billToName; }
    public void setBillToName(String v) { this.billToName = v; }
    public String getBillToAddress() { return billToAddress; }
    public void setBillToAddress(String v) { this.billToAddress = v; }
    public String getBillToGst() { return billToGst; }
    public void setBillToGst(String v) { this.billToGst = v; }
    public String getBillToState() { return billToState; }
    public void setBillToState(String v) { this.billToState = v; }
    public String getShipToDetails() { return shipToDetails; }
    public void setShipToDetails(String v) { this.shipToDetails = v; }
    public String getShipToGst() { return shipToGst; }
    public void setShipToGst(String v) { this.shipToGst = v; }
    public String getShipToState() { return shipToState; }
    public void setShipToState(String v) { this.shipToState = v; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double v) { this.totalAmount = v; }
    public List<InvoiceItemEntity> getItems() { return items; }
    public void setItems(List<InvoiceItemEntity> v) { this.items = v; }
}