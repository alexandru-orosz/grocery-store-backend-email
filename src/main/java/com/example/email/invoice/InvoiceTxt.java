package com.example.email.invoice;

import com.example.email.dto.Address;
import com.example.email.dto.Customer;
import com.example.email.dto.OrderItem;
import com.example.email.dto.Purchase;
import com.example.email.utils.Utils;
import lombok.SneakyThrows;

import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class InvoiceTxt implements InvoiceStrategy {

    private Purchase purchase;

    @SneakyThrows
    @Override
    public String generateInvoice(Purchase purchase) {
        this.purchase = purchase;

        final String filename = "invoices/" + purchase.getOrder().getOrderTrackingNumber() + ".txt";

        FileWriter writer = new FileWriter(filename);

        writer.write(this.setTitle());
        writer.write(this.setHeader());
        writer.write(this.setInvoiceDetails());
        writer.write(this.setBillTo());
        writer.write(this.setShipTo());
        this.setProducts(writer);
        writer.write(this.setTotal());

        writer.close();

        return filename;
    }

    private String setTitle() {
        return "\n ------------------------------------- INVOICE -------------------------------------\n\n\n";
    }

    private String setHeader() {
        return " 47 Main St, Las Vegas, NV, 59726\n" +
                " www.grocery-store.com\n" +
                " 990-120-4560\n" +
                " contact@grocery-store.com\n\n\n";
    }

    private String setInvoiceDetails() {
        String invoiceNo = " Invoice No: " + UUID.randomUUID().toString() + "\n";
        LocalDateTime date = LocalDateTime.now();
        String invoiceDate = " Invoice date: " + Utils.formatDate(date) + "\n\n";
        return invoiceNo + invoiceDate;
    }

    private String setBillTo() {
        Customer customer = this.purchase.getCustomer();
        Address billingAddress = this.purchase.getBillingAddress();

        String customerName = customer.getFirstName() + " " + customer.getLastName();
        String customerEmail = this.purchase.getCustomer().getEmail();

        return " BILL TO:\n " +
                customerName + "\n " +
                customerEmail + "\n " +
                billingAddress.getStreet() + ", " +
                billingAddress.getCity() + ", " +
                billingAddress.getState() + " " +
                billingAddress.getZipCode() + ", " +
                billingAddress.getCountry() + "\n\n";
    }

    private String setShipTo() {
        Address shippingAddress = this.purchase.getShippingAddress();

        return " SHIP TO:\n " +
                shippingAddress.getStreet() + ", " +
                shippingAddress.getCity() + ", " +
                shippingAddress.getState() + " " +
                shippingAddress.getZipCode() + ", " +
                shippingAddress.getCountry() + "\n\n\n";
    }

    @SneakyThrows
    private void setProducts(FileWriter writer) {

        writer.write(" +-----------------------------------+--------------+----------------+------------+\n");
        writer.write(" |            DESCRIPTION            |   QUANTITY   |   UNIT PRICE   |   AMOUNT   |\n");
        writer.write(" +-----------------------------------+--------------+----------------+------------+\n");

        Set<OrderItem>  orderItems = this.purchase.getOrderItems();

        for (OrderItem orderItem : orderItems) {
            String description = String.format("%-33s", orderItem.getProductName());
            String quantity = String.format("%-12s", orderItem.getQuantity());
            String unitPrice = String.format("%-14s", "$" + orderItem.getUnitPrice().setScale(2, RoundingMode.FLOOR));
            String amount = String.format("%-10s", "$" + BigDecimal.valueOf(orderItem.getQuantity()).multiply(orderItem.getUnitPrice()).setScale(2, RoundingMode.FLOOR));

            writer.write(" | " + description + " | " + quantity + " | " + unitPrice + " | " + amount + " |\n");
            writer.write(" +-----------------------------------+--------------+----------------+------------+\n");
        }

    }

    private String setTotal() {
        return  "\n SHIPPING: FREE\n" +
                " TOTAL: $" + this.purchase.getOrder().getTotalPrice().setScale(2, RoundingMode.FLOOR) +"\n";
    }
}
