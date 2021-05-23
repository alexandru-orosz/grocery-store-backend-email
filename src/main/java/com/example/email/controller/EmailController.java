package com.example.email.controller;


import com.example.email.dto.Purchase;
import com.example.email.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:8080")
public class EmailController {

    private final EmailService emailService;
    private final Logger logger = Logger.getLogger(EmailController.class.getName());

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody Purchase purchase) {
        emailService.sendEmail(purchase);
        logger.log(Level.INFO, "Email send. Tracking number: " + purchase.getOrder().getOrderTrackingNumber());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
