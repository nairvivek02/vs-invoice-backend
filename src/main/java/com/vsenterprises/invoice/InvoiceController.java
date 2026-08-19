package com.vsenterprises.invoice;

import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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