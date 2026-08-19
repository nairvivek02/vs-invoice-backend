package com.vsenterprises.invoice;

import java.util.List;

public class InvoiceData {
    private String invoiceNo;
    private String invoiceDate;
    private String poNo = "";
    private String poDate = "";
    private String stateCode = "27";

    private String vendorCode = "";
    private String transportMode = "";
    private String vehicleNumber = "";
    private String dateOfSupply = "";
    private String placeOfSupply = "";

    private String companyAddress = "Sr.No 23/2E, Ramnagar, Balewadi, Near C.M International School, Pune- 411 045.";
    private String companyGstNo = "27GVGPP3345J1Z4";

    private String billToName;
    private String billToAddress;
    private String billToGst;
    private String billToState = "";
    private String shipToDetails;
    private String shipToGst = "";
    private String shipToState = "";

    private List<InvoiceItem> items;

    public String getInvoiceNo() { return invoiceNo; }
    public void setInvoiceNo(String v) { this.invoiceNo = v; }
    public String getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(String v) { this.invoiceDate = v; }
    public String getPoNo() { return poNo; }
    public void setPoNo(String v) { this.poNo = v; }
    public String getPoDate() { return poDate; }
    public void setPoDate(String v) { this.poDate = v; }
    public String getStateCode() { return stateCode; }
    public void setStateCode(String v) { this.stateCode = v; }

    public String getVendorCode() { return vendorCode; }
    public void setVendorCode(String v) { this.vendorCode = v; }
    public String getTransportMode() { return transportMode; }
    public void setTransportMode(String v) { this.transportMode = v; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String v) { this.vehicleNumber = v; }
    public String getDateOfSupply() { return dateOfSupply; }
    public void setDateOfSupply(String v) { this.dateOfSupply = v; }
    public String getPlaceOfSupply() { return placeOfSupply; }
    public void setPlaceOfSupply(String v) { this.placeOfSupply = v; }

    public String getCompanyAddress() { return companyAddress; }
    public void setCompanyAddress(String v) { this.companyAddress = v; }
    public String getCompanyGstNo() { return companyGstNo; }
    public void setCompanyGstNo(String v) { this.companyGstNo = v; }
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
    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> v) { this.items = v; }
}
