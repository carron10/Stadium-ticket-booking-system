package com.ticket.TicketSystem;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.ticket.TicketSystem.mail.EmailServiceImpl;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.core.env.Environment;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class EmailServiceTest extends BaseTest {

    @Autowired
    private EmailServiceImpl emailService;

    private GreenMail greenMail;
    
    @Autowired
    private Environment env;

    @BeforeEach
    public void startMailServer() {
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "test"));
        greenMail.start();

        // Configure the JavaMailSenderImpl with GreenMail settings
        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) emailService.getMailSender();
        mailSender.setHost("localhost");
        mailSender.setPort(3025);
        mailSender.setUsername("test");
        mailSender.setPassword("test");
        mailSender.getJavaMailProperties().setProperty("mail.transport.protocol", "smtp");
        mailSender.getJavaMailProperties().setProperty("mail.smtp.auth", "false");
        mailSender.getJavaMailProperties().setProperty("mail.smtp.starttls.enable", "false");
        
    }

    @AfterEach
    public void stopMailServer() {
        greenMail.stop();
    }

   

    @Test
    void testActiveProfile() {
        String[] activeProfiles = env.getActiveProfiles();
        assertThat(activeProfiles).contains("test");
    }
    @Test
    public void testSendSimpleEmail()
            throws InterruptedException, MessagingException {

        String to = "test@localhost.com";
        String subject = "Test Subject";
        String mailText = "Test Email Text";
        
        assertEquals(true,greenMail.isRunning() );

        emailService.sendSimpleEmail(to, subject, mailText);

        assertTrue(greenMail.waitForIncomingEmail(5000, 1));
        Message[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length);
        assertEquals(subject, messages[0].getSubject());  // Check the subject instead of body
        String body = GreenMailUtil.getBody(messages[0]).replaceAll("=\r?\n", "");
        assertEquals(mailText, body);
    }

}
