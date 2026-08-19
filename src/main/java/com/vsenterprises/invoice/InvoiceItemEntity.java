package com.vsenterprises.invoice;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "invoice_items")
public class InvoiceItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private String hsnCode;
    private String uom;
    private int qty;
    private double rate;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    @JsonIgnore
    private InvoiceEntity invoice;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public InvoiceEntity getInvoice() { return invoice; }
    public void setInvoice(InvoiceEntity v) { this.invoice = v; }
}