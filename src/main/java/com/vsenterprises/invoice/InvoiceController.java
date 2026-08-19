package com.vsenterprises.invoice;

import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    private final InvoicePdfService pdfService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    public InvoiceController(InvoicePdfService pdfService) {
        this.pdfService = pdfService;
    }

    // React Native app POSTs invoice JSON here, gets back a PDF byte stream
    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateInvoice(@RequestBody InvoiceData data) {
        try {
            byte[] pdf = pdfService.generateInvoice(data);

            // Save invoice to database
            saveInvoiceToDb(data);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "invoice_" + data.getInvoiceNo().replace("/", "-") + ".pdf");
            return ResponseEntity.ok().headers(headers).body(pdf);
        } catch (DocumentException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Returns saved invoice history for the app to display
    @GetMapping
    public List<InvoiceEntity> getAllInvoices() {
        return invoiceRepository.findAllByOrderByIdDesc();
    }
    
    // Regenerates the PDF for a previously saved invoice, by its database id
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getInvoicePdf(@PathVariable Long id) {
        InvoiceEntity entity = invoiceRepository.findById(id).orElse(null);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            InvoiceData data = new InvoiceData();
            data.setInvoiceNo(entity.getInvoiceNo());
            data.setInvoiceDate(entity.getInvoiceDate());
            data.setPoNo(entity.getPoNo());
            data.setPoDate(entity.getPoDate());
            data.setBillToName(entity.getBillToName());
            data.setBillToAddress(entity.getBillToAddress());
            data.setBillToGst(entity.getBillToGst());
            data.setBillToState(entity.getBillToState());
            data.setShipToDetails(entity.getShipToDetails());
            data.setShipToGst(entity.getShipToGst());
            data.setShipToState(entity.getShipToState());

            List<InvoiceItem> items = entity.getItems().stream().map(ie -> {
                InvoiceItem item = new InvoiceItem();
                item.setDescription(ie.getDescription());
                item.setHsnCode(ie.getHsnCode());
                item.setUom(ie.getUom());
                item.setQty(ie.getQty());
                item.setRate(ie.getRate());
                return item;
            }).collect(Collectors.toList());
            data.setItems(items);

            byte[] pdf = pdfService.generateInvoice(data);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "invoice_" + entity.getInvoiceNo().replace("/", "-") + ".pdf");
            return ResponseEntity.ok().headers(headers).body(pdf);
        } catch (DocumentException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
 // Deletes a saved invoice by its database id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        if (!invoiceRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        invoiceRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void saveInvoiceToDb(InvoiceData data) {
        InvoiceEntity entity = new InvoiceEntity();
        entity.setInvoiceNo(data.getInvoiceNo());
        entity.setInvoiceDate(data.getInvoiceDate());
        entity.setPoNo(data.getPoNo());
        entity.setPoDate(data.getPoDate());
        entity.setBillToName(data.getBillToName());
        entity.setBillToAddress(data.getBillToAddress());
        entity.setBillToGst(data.getBillToGst());
        entity.setBillToState(data.getBillToState());
        entity.setShipToDetails(data.getShipToDetails());
        entity.setShipToGst(data.getShipToGst());
        entity.setShipToState(data.getShipToState());

        double total = 0;
        List<InvoiceItemEntity> itemEntities = data.getItems().stream().map(item -> {
            InvoiceItemEntity ie = new InvoiceItemEntity();
            ie.setDescription(item.getDescription());
            ie.setHsnCode(item.getHsnCode());
            ie.setUom(item.getUom());
            ie.setQty(item.getQty());
            ie.setRate(item.getRate());
            ie.setInvoice(entity);
            return ie;
        }).collect(Collectors.toList());

        for (InvoiceItemEntity ie : itemEntities) {
            total += ie.getQty() * ie.getRate();
        }

        entity.setItems(itemEntities);
        entity.setTotalAmount(total);

        invoiceRepository.save(entity);
    }
}