package com.example.email.email;

import com.example.email.dto.Customer;
import com.example.email.dto.Purchase;
import com.example.email.invoice.Invoice;
import com.example.email.invoice.InvoicePdf;
import com.example.email.invoice.InvoiceTxt;
import com.example.email.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @SneakyThrows
    public void sendEmail(Purchase purchase) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

        Customer customer = purchase.getCustomer();
        LocalDateTime date = LocalDateTime.now();

        final String from = "grocery@store.com";
        final String email = customer.getEmail();
        final String body = customer.getFirstName() + " " + customer.getLastName() + ", thank you for your order!\n\n" +
                "We've received your order and will contact you as soon as your package is shipped.\n" +
                "You can find your purchase information in the attached invoice." + "\n\n" +
                "Order tracking number: " + purchase.getOrder().getOrderTrackingNumber()  + "\n" +
                "Order date: " + Utils.formatDate(date) + "\n\n" +
                "If you have any questions about your order, please contact us.";
        final String subject = "Order Received - Grocery Store";

        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setText(body);
        mimeMessageHelper.setSubject(subject);

        final String invoiceFilename = this.generateInvoice(purchase);
        FileSystemResource fileSystem = new FileSystemResource(new File(invoiceFilename));

        mimeMessageHelper.addAttachment(Objects.requireNonNull(fileSystem.getFilename()), fileSystem);

        this.mailSender.send(mimeMessage);

    }

    private String generateInvoice(Purchase purchase) {

        Invoice invoice = null;

        if(purchase.getInvoice().equals("PDF")) {
            invoice = new Invoice(new InvoicePdf());
        }

        if(purchase.getInvoice().equals("TXT")){
            invoice = new Invoice((new InvoiceTxt()));
        }

        if(invoice != null) {
            return invoice.generateInvoice(purchase);
        }

        return "";
    }

}
