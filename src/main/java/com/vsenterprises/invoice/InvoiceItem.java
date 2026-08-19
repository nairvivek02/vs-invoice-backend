package com.vsenterprises.invoice;

public class InvoiceItem {
    private String description;
    private String hsnCode;
    private String uom;
    private int qty;
    private double rate;

    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }
    public String getHsnCode() { return hsnCode; }
    public void setHsnCode(String v) { this.hsnCode = v; }
    public String getUom() { return uom; }
    public void setUom(String v) { this.uom = v; }
    public int getQty() { return qty; }
    public void setQty(int v) { this.qty = v; }
    public double getRate() { return rate; }
    public void setRate(double v) { this.rate = v; }
}