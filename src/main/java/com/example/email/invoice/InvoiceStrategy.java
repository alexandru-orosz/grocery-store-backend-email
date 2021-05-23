package com.example.email.invoice;

import com.example.email.dto.Purchase;

public interface InvoiceStrategy {
    String generateInvoice(Purchase purchase);
}

