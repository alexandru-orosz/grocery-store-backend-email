package com.example.email.invoice;

import com.example.email.dto.Purchase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Invoice {

    private final InvoiceStrategy invoiceStrategy;

    public String generateInvoice(Purchase purchase) {
        return invoiceStrategy.generateInvoice(purchase);
    }

}
