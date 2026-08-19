package com.vsenterprises.invoice;
 
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;
 
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
 
@Service
public class InvoicePdfService {
 
    private int headerRowCount = 0;
 
    // ---- Fixed company banking details (same on every invoice) ----
    private static final String PAN_NO = "GVGPP3345J";
    private static final String BANK_NAME_LINE = "V S ENTERPRISES";
    private static final String BANK_NAME = "HDFC BANK";
    private static final String ACCOUNT_NO = "50200096743517";
    private static final String BRANCH = "Balewadi, PUNE-411045";
    private static final String IFSC_CODE = "HDFC0005179";
 
    public byte[] generateInvoice(InvoiceData data) throws DocumentException {
 
        Document document = new Document(PageSize.A4, 30, 30, 30, 30);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();
 
        PdfPTable table = new PdfPTable(new float[]{5, 35, 13, 8, 7, 12, 15});
        table.setWidthPercentage(100);
        headerRowCount = 0;
 
        addLetterheadBlock(table, data);
        addInvoiceMetaBlock(table, data);
        addPartyBlock(table, data);
        addColumnHeaderRow(table);
 
        table.setHeaderRows(headerRowCount);
 
        addItemRows(table, data);
        addTotalsBlock(table, data);
        addBankAndSignatureBlock(table, data);
 
        document.add(table);
        document.close();
 
        return baos.toByteArray();
    }
 
    private void addLetterheadBlock(PdfPTable table, InvoiceData data) {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
        Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22);
        Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
 
        PdfPCell outerCell = new PdfPCell();
        outerCell.setColspan(7);
        outerCell.setPadding(4);
 
        // Inner 3-column layout: logo | centered text | empty spacer (same width as logo)
        // The matching spacer keeps the text block truly centered in the box.
        PdfPTable inner = new PdfPTable(new float[]{29, 42, 29});
        try {
            inner.setWidthPercentage(100);
        } catch (Exception ignored) {
        }
 
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setVerticalAlignment(Element.ALIGN_TOP);
        logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        Image logo = loadLogo();
        if (logo != null) {
            logo.scaleToFit(80, 80);
            logoCell.addElement(logo);
        }
        inner.addCell(logoCell);
 
        PdfPCell textCell = new PdfPCell();
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setHorizontalAlignment(Element.ALIGN_CENTER);
 
        Paragraph titleP = new Paragraph("TAX INVOICE CUM DELIVERY CHALLAN", titleFont);
        titleP.setAlignment(Element.ALIGN_CENTER);
        textCell.addElement(titleP);
 
        Paragraph nameP = new Paragraph("V S ENTERPRISES", nameFont);
        nameP.setAlignment(Element.ALIGN_CENTER);
        textCell.addElement(nameP);
 
        Paragraph addrP = new Paragraph(data.getCompanyAddress(), subFont);
        addrP.setAlignment(Element.ALIGN_CENTER);
        textCell.addElement(addrP);
 
        Paragraph gstP = new Paragraph("GST No.: " + data.getCompanyGstNo(), subFont);
        gstP.setAlignment(Element.ALIGN_CENTER);
        textCell.addElement(gstP);
 
        inner.addCell(textCell);
 
        PdfPCell spacerCell = new PdfPCell(new Phrase(""));
        spacerCell.setBorder(Rectangle.NO_BORDER);
        inner.addCell(spacerCell);
 
        outerCell.addElement(inner);
        table.addCell(outerCell);
        headerRowCount++;
    }
 
    // Loads logo.png from the classpath (src/main/resources/logo.png).
    // Returns null (and the letterhead just skips the logo) if it's missing or fails to load,
    // so a missing file never breaks invoice generation.
    private Image loadLogo() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("logo.png")) {
            if (is == null) return null;
            byte[] bytes = is.readAllBytes();
            return Image.getInstance(bytes);
        } catch (Exception e) {
            return null;
        }
    }
 
    private void addInvoiceMetaBlock(PdfPTable table, InvoiceData data) {
        Font f = FontFactory.getFont(FontFactory.HELVETICA, 9);
 
        PdfPCell left = new PdfPCell();
        left.setColspan(3);
        left.setPadding(4);
        left.addElement(new Paragraph("Invoice No. : " + data.getInvoiceNo(), f));
        left.addElement(new Paragraph("Invoice Date : " + data.getInvoiceDate(), f));
        left.addElement(new Paragraph("PO No. : " + emptyIfNull(data.getPoNo()), f));
        left.addElement(new Paragraph("PO Date. : " + emptyIfNull(data.getPoDate()), f));
        left.addElement(new Paragraph("State Code : " + emptyIfNull(data.getStateCode()), f));
        table.addCell(left);
 
        PdfPCell right = new PdfPCell();
        right.setColspan(4);
        right.setPadding(4);
        right.addElement(new Paragraph("Vendor Code if any : " + emptyIfNull(data.getVendorCode()), f));
        right.addElement(new Paragraph("Transport Mode : " + emptyIfNull(data.getTransportMode()), f));
        right.addElement(new Paragraph("Vehicle number : " + emptyIfNull(data.getVehicleNumber()), f));
        right.addElement(new Paragraph("Date of Supply : " + emptyIfNull(data.getDateOfSupply()), f));
        right.addElement(new Paragraph("Place of Supply : " + emptyIfNull(data.getPlaceOfSupply()), f));
        table.addCell(right);
 
        headerRowCount++;
    }
 
    private void addPartyBlock(PdfPTable table, InvoiceData data) {
        Font bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
        Font f = FontFactory.getFont(FontFactory.HELVETICA, 9);
 
        PdfPCell billHeader = new PdfPCell(new Phrase("BILL TO PARTY", bold));
        billHeader.setColspan(3);
        billHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(billHeader);
 
        PdfPCell shipHeader = new PdfPCell(new Phrase("SHIP TO PARTY", bold));
        shipHeader.setColspan(4);
        shipHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(shipHeader);
        headerRowCount++;
 
        PdfPCell billCell = new PdfPCell();
        billCell.setColspan(3);
        billCell.setPadding(4);
        billCell.addElement(new Paragraph(data.getBillToName(), bold));
        billCell.addElement(new Paragraph(data.getBillToAddress(), f));
        billCell.addElement(new Paragraph(
                "GST No. " + emptyIfNull(data.getBillToGst())
                + "    State : " + emptyIfNull(data.getBillToState())
                + "    Code:-" + emptyIfNull(data.getStateCode()), f));
        table.addCell(billCell);
 
        PdfPCell shipCell = new PdfPCell();
        shipCell.setColspan(4);
        shipCell.setPadding(4);
        shipCell.addElement(new Paragraph(data.getShipToDetails() != null
                ? data.getShipToDetails() : "", f));
        shipCell.addElement(new Paragraph(" ", f));
        shipCell.addElement(new Paragraph(
                "GST No. " + emptyIfNull(data.getShipToGst())
                + "    State : " + emptyIfNull(data.getShipToState())
                + "    Code:-" + emptyIfNull(data.getStateCode()), f));
        table.addCell(shipCell);
        headerRowCount++;
    }
 
    private void addColumnHeaderRow(PdfPTable table) {
        Font bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
        String[] headers = {"Sr.No.", "Particulars", "HSN/SAC CODE", "UOM", "QTY", "Rate Rs.", "Amount Rs."};
        for (String h : headers) {
            PdfPCell c = new PdfPCell(new Phrase(h, bold));
            c.setHorizontalAlignment(Element.ALIGN_CENTER);
            c.setPadding(4);
            table.addCell(c);
        }
        headerRowCount++;
    }
 
    private void addItemRows(PdfPTable table, InvoiceData data) {
        Font f = FontFactory.getFont(FontFactory.HELVETICA, 9);
        int sr = 1;
        for (InvoiceItem item : data.getItems()) {
            table.addCell(cell(String.valueOf(sr++), f, Element.ALIGN_CENTER));
            table.addCell(cell(item.getDescription(), f, Element.ALIGN_LEFT));
            table.addCell(cell(item.getHsnCode(), f, Element.ALIGN_CENTER));
            table.addCell(cell(item.getUom(), f, Element.ALIGN_CENTER));
            table.addCell(cell(String.valueOf(item.getQty()), f, Element.ALIGN_CENTER));
            table.addCell(cell(String.valueOf(item.getRate()), f, Element.ALIGN_RIGHT));
            table.addCell(cell(String.format("%.2f", item.getQty() * item.getRate()), f, Element.ALIGN_RIGHT));
        }
    }
 
    private double lastGrandTotal = 0;
 
    private void addTotalsBlock(PdfPTable table, InvoiceData data) {
        Font bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
 
        double subTotal = data.getItems().stream()
                .mapToDouble(i -> i.getQty() * i.getRate()).sum();
        double cgst = round2(subTotal * 0.09);
        double sgst = round2(subTotal * 0.09);
        double grandTotal = round2(subTotal + cgst + sgst);
        lastGrandTotal = grandTotal;
 
        addTotalRow(table, "Sub Total", subTotal, bold);
        addTotalRow(table, "CGST 9%", cgst, bold);
        addTotalRow(table, "SGST 9%", sgst, bold);
        addTotalRow(table, "Total Rs. (Round off)", grandTotal, bold);
    }
 
    private void addTotalRow(PdfPTable table, String label, double value, Font f) {
        PdfPCell blank = new PdfPCell(new Phrase(""));
        blank.setColspan(5);
        blank.setBorder(Rectangle.NO_BORDER);
        table.addCell(blank);
 
        PdfPCell labelCell = new PdfPCell(new Phrase(label, f));
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(labelCell);
 
        PdfPCell valueCell = new PdfPCell(new Phrase(String.format("%.2f", value), f));
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }
 
    private void addBankAndSignatureBlock(PdfPTable table, InvoiceData data) {
        Font bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
        Font f = FontFactory.getFont(FontFactory.HELVETICA, 9);
 
        // Amount in words row (full width)
        PdfPCell wordsCell = new PdfPCell(
                new Phrase("Rupees " + amountInWords(Math.round(lastGrandTotal)) + " Only", bold));
        wordsCell.setColspan(7);
        wordsCell.setPadding(5);
        table.addCell(wordsCell);
 
        // Bank details (left) + PAN (left, under bank) and Signature blocks (right)
        PdfPCell bankCell = new PdfPCell();
        bankCell.setColspan(4);
        bankCell.setPadding(5);
        bankCell.addElement(new Paragraph("Banking Name : " + BANK_NAME_LINE, f));
        bankCell.addElement(new Paragraph("Name of Bank : " + BANK_NAME, f));
        bankCell.addElement(new Paragraph("Account No. (Current) : " + ACCOUNT_NO, f));
        bankCell.addElement(new Paragraph("Branch : " + BRANCH, f));
        bankCell.addElement(new Paragraph("IFSC Code : " + IFSC_CODE, f));
        bankCell.addElement(new Paragraph("PAN No. : " + PAN_NO, f));
        table.addCell(bankCell);
 
        PdfPCell signCell = new PdfPCell();
        signCell.setColspan(3);
        signCell.setPadding(5);
        signCell.setMinimumHeight(70);
        Paragraph certified = new Paragraph(
                "Ceritified that the particulars given above are true and correct", f);
        certified.setAlignment(Element.ALIGN_RIGHT);
        signCell.addElement(certified);
        Paragraph forCompany = new Paragraph("For V S ENTERPRISES", bold);
        forCompany.setAlignment(Element.ALIGN_RIGHT);
        signCell.addElement(forCompany);
        signCell.addElement(new Paragraph(" ", f));
        signCell.addElement(new Paragraph(" ", f));
        Paragraph proprietor = new Paragraph("Proprietor", f);
        proprietor.setAlignment(Element.ALIGN_RIGHT);
        signCell.addElement(proprietor);
        table.addCell(signCell);
 
        // Receiver's signature line (full width)
        PdfPCell receiverCell = new PdfPCell(
                new Phrase("Receiver's Signature & Remarks if Any:", f));
        receiverCell.setColspan(7);
        receiverCell.setPadding(5);
        receiverCell.setMinimumHeight(40);
        table.addCell(receiverCell);
    }
 
    private PdfPCell cell(String text, Font f, int align) {
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setHorizontalAlignment(align);
        c.setPadding(3);
        return c;
    }
 
    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
 
    private String emptyIfNull(String s) {
        return s == null ? "" : s;
    }
 
    // ---- Amount in words (Indian numbering: Crore/Lakh/Thousand) ----
    private static final String[] ONES = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
            "Seventeen", "Eighteen", "Nineteen"
    };
    private static final String[] TENS = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };
 
    private String amountInWords(long number) {
        if (number == 0) return "Zero";
        StringBuilder result = new StringBuilder();
 
        long crore = number / 10000000;
        number %= 10000000;
        long lakh = number / 100000;
        number %= 100000;
        long thousand = number / 1000;
        number %= 1000;
        long hundred = number / 100;
        long rest = number % 100;
 
        if (crore > 0) result.append(twoDigit((int) crore)).append(" Crore ");
        if (lakh > 0) result.append(twoDigit((int) lakh)).append(" Lakh ");
        if (thousand > 0) result.append(twoDigit((int) thousand)).append(" Thousand ");
        if (hundred > 0) result.append(ONES[(int) hundred]).append(" Hundred ");
        if (rest > 0) result.append(twoDigit((int) rest)).append(" ");
 
        return result.toString().trim();
    }
 
    private String twoDigit(int n) {
        if (n < 20) return ONES[n];
        return (TENS[n / 10] + " " + ONES[n % 10]).trim();
    }
}
 