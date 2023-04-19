package com.ezticket.core.service;

import com.ezticket.core.pojo.EmailDetails;
import jakarta.mail.MessagingException;

public interface EmailService {
    // Method
    // To send a simple email
    String sendSimpleMail(EmailDetails details);

    // Method
    // To send an email with attachment
    String sendMailWithAttachment(EmailDetails details);

    String sendOrderMail(String name, String to, String porderno,String status);

    String sendFollowProductSale(String name, String to, String pname, String specialPrice,String productno) throws MessagingException;
}
