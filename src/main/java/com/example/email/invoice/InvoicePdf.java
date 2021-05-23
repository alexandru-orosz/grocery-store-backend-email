package com.example.email.invoice;

import com.example.email.dto.Address;
import com.example.email.dto.Customer;
import com.example.email.dto.OrderItem;
import com.example.email.dto.Purchase;
import com.example.email.utils.Utils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.SneakyThrows;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class InvoicePdf implements InvoiceStrategy {

    private Purchase purchase;

    @SneakyThrows
    @Override
    public String generateInvoice(Purchase purchase) {
        this.purchase = purchase;

        final String filename = "invoices/" + purchase.getOrder().getOrderTrackingNumber() + ".pdf";
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();

        document.add(this.setTitle());
        document.add(this.setCompanyName());
        document.add(this.setCompanyDetails());
        document.add(this.setInvoiceDetails());
        document.add(this.setBillTo());
        document.add(this.setBillToDetails());
        document.add(this.setShipTo());
        document.add(this.setShipToDetails());
        document.add(new Paragraph("\n\n"));
        document.add(this.setTable());
        document.add(this.setTotal());
        document.add(this.setImage());

        document.close();
        return filename;
    }

    private Paragraph setTitle() {
        Font font = FontFactory.getFont(FontFactory.TIMES, 20, BaseColor.BLACK);
        Paragraph title = new Paragraph("INVOICE", font);
        title.setAlignment(Element.TITLE);
        return title;
    }

    private Paragraph setCompanyName() {
        Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, 18, BaseColor.BLACK);
        Paragraph companyName = new Paragraph("\n\nGrocery Store", font);
        companyName.setAlignment(Element.ALIGN_LEFT);
        return companyName;
    }

    private Paragraph setCompanyDetails() {
        Font font = FontFactory.getFont(FontFactory.TIMES, 12, BaseColor.BLACK);
        String address = "47 Main St, Las Vegas, NV, 59726";
        String site = "www.grocery-store.com";
        String phone = "990-120-4560";
        String email = "contact@grocery-store.com";

        String companyDetailsString = address + "\n" + site + "\n" + phone + "\n" + email;

        Paragraph companyDetails = new Paragraph(companyDetailsString, font);
        companyDetails.setAlignment(Element.ALIGN_LEFT);
        return companyDetails;
    }

    private Paragraph setInvoiceDetails() {
        Font font = FontFactory.getFont(FontFactory.TIMES, 12, BaseColor.BLACK);

        String invoiceNo = "\n\nInvoice No: " + UUID.randomUUID().toString();

        LocalDateTime date = LocalDateTime.now();
        String invoiceDate = "\nInvoice date: " + Utils.formatDate(date);

        Paragraph invoiceDetails = new Paragraph(invoiceNo + invoiceDate, font);
        invoiceDetails.setAlignment(Element.ALIGN_LEFT);
        return invoiceDetails;
    }

    private Paragraph setBillTo() {
        Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK);
        Paragraph billTo = new Paragraph("\n\nBILL TO:", font);
        billTo.setAlignment(Element.ALIGN_LEFT);
        return billTo;
    }

    private Paragraph setBillToDetails() {
        Font font = FontFactory.getFont(FontFactory.TIMES, 12, BaseColor.BLACK);

        Customer customer = this.purchase.getCustomer();
        Address billingAddress = this.purchase.getBillingAddress();

        String customerName = customer.getFirstName() + " " + customer.getLastName();
        String customerEmail = this.purchase.getCustomer().getEmail();

        String billingAddressString = billingAddress.getStreet() + ", " +
                billingAddress.getCity() + ", " +
                billingAddress.getState() + " " +
                billingAddress.getZipCode() + ", " +
                billingAddress.getCountry();

        String billToString = customerName + "\n" +
                customerEmail + "\n" +
                billingAddressString;

        Paragraph billToDetails = new Paragraph(billToString, font);
        billToDetails.setAlignment(Element.ALIGN_LEFT);
        return billToDetails;
    }

    private Paragraph setShipTo() {
        Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK);
        Paragraph shipTo = new Paragraph("\nSHIP TO:", font);
        shipTo.setAlignment(Element.ALIGN_LEFT);
        return shipTo;
    }

    private Paragraph setShipToDetails() {
        Font font = FontFactory.getFont(FontFactory.TIMES, 12, BaseColor.BLACK);
        Address shippingAddress = this.purchase.getShippingAddress();

        String shippingAddressString = shippingAddress.getStreet() + ", " +
                shippingAddress.getCity() + ", " +
                shippingAddress.getState() + " " +
                shippingAddress.getZipCode() + ", " +
                shippingAddress.getCountry();

        Paragraph shipToDetails = new Paragraph(shippingAddressString, font);
        shipToDetails.setAlignment(Element.ALIGN_LEFT);
        return shipToDetails;
    }

    @SneakyThrows
    private Image setImage() {
        String path = "src/main/resources/images/logo.png";
        Image image = Image.getInstance(path);
        image.setAbsolutePosition(395,580);
        return image;
    }

    private PdfPTable setTable() {
        PdfPTable table = new PdfPTable(4);
        this.addTableHeader(table);
        this.addRows(table);
        table.setWidthPercentage(100);
        return table;
    }

    private void addTableHeader(PdfPTable table) {
        String[] columns = {"DESCRIPTION", "QUANTITY", "UNIT PRICE", "AMOUNT"};

        Stream.of(columns)
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private void addRows(PdfPTable table) {

        Set<OrderItem> orderItems = this.purchase.getOrderItems();

        orderItems.forEach(orderItem -> {
            table.addCell(orderItem.getProductName());
            table.addCell(String.valueOf(orderItem.getQuantity()));
            table.addCell("$" + orderItem.getUnitPrice().setScale(2, RoundingMode.FLOOR));
            table.addCell("$" + BigDecimal.valueOf(orderItem.getQuantity()).multiply(orderItem.getUnitPrice()).setScale(2, RoundingMode.FLOOR));
        });
    }

    private Paragraph setTotal() {
        Font font = FontFactory.getFont(FontFactory.TIMES, 12, BaseColor.BLACK);
        String totalString = "\nSHIPPING: FREE\nTOTAL: $" + this.purchase.getOrder().getTotalPrice().setScale(2, RoundingMode.FLOOR);

        Paragraph total = new Paragraph(totalString, font);
        total.setAlignment(Element.ALIGN_RIGHT);
        return total;
    }

}