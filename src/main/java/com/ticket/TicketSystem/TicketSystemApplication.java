package com.ticket.TicketSystem;

import com.ticket.TicketSystem.mail.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootApplication
@RestController
public class TicketSystemApplication implements CommandLineRunner {

//    @Autowired
//    private JavaMailSender javaMailSender;
//
//    @Autowired
//    private Environment env;
//    
//    @Autowired
//    EmailServiceImpl emailService;

    public static void main(String[] args) {
        SpringApplication.run(TicketSystemApplication.class, args);
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
    
    @Override
    public void run(String... args) throws Exception {
//         System.out.println("Log Level: " + env.getProperty("logging.level.org.springframework.mail"));
//         emailService.sendSimpleEmail("carronmuleya10@gmail.com", "Test email Hello", "I am testing this server");
//         System.out.println("Email sent");
//        printMailConfiguration();
//        sendSimpleEmail("carronmuleya10@gmail.com", "Test Subject", "Test Email Body");
    }
//
//    public void sendSimpleEmail(String to, String subject, String text) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(text);
//        javaMailSender.send(message);
//    }
//
//    private void printMailConfiguration() {
//        System.out.println("Mail Configuration:");
//        System.out.println("Host: " + env.getProperty("spring.mail.host"));
//        System.out.println("Port: " + env.getProperty("spring.mail.port"));
//        System.out.println("Username: " + env.getProperty("spring.mail.username"));
//        System.out.println("Password: " + env.getProperty("spring.mail.password"));
//        System.out.println("SMTP Auth: " + env.getProperty("spring.mail.properties.mail.smtp.auth"));
//        System.out.println("StartTLS Enable: " + env.getProperty("spring.mail.properties.mail.smtp.starttls.enable"));
//        System.out.println("StartTLS Required: " + env.getProperty("spring.mail.properties.mail.smtp.starttls.required"));
//        System.out.println("Protocol: " + env.getProperty("spring.mail.properties.mail.transport.protocol"));
//    }
}

/**
 * Plan Tables 1. Tickets[to store tickets prices,types and the number
 * available] 2. User[to store usernames, addresses and etc] 3. UserMeta [To
 * store other details for users] 3. Payments [to store tokens,and payment
 * details] 4. Games [To store games, and their due dates] 5. Orders [to store
 * ticket orders] 6. OrderMeta [To store meta data about an order]
 */
