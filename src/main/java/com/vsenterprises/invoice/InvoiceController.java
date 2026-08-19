package com.vsenterprises.invoice;

import com.lowagie.text.DocumentException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    private final InvoicePdfService pdfService;

    public InvoiceController(InvoicePdfService pdfService) {
        this.pdfService = pdfService;
    }

    // React Native app POSTs invoice JSON here, gets back a PDF byte stream
    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateInvoice(@RequestBody InvoiceData data) {
        try {
            byte[] pdf = pdfService.generateInvoice(data);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "invoice_" + data.getInvoiceNo().replace("/", "-") + ".pdf");
            return ResponseEntity.ok().headers(headers).body(pdf);
        } catch (DocumentException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}