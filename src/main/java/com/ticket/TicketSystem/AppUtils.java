package com.ticket.TicketSystem;




import com.google.zxing.WriterException;
import com.ticket.TicketSystem.CustomElementFactoryImpl;
import com.ticket.TicketSystem.QRCodeGenerator;
import com.ticket.TicketSystem.entities.Order;
import com.ticket.TicketSystem.repositories.ContactUsRepository;
import com.ticket.TicketSystem.repositories.GameRepository;
import com.ticket.TicketSystem.repositories.GameTicketRepository;
import com.ticket.TicketSystem.repositories.OrderRepository;
import com.ticket.TicketSystem.repositories.PaymentResultsRepo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Carron Muleya
 */
@Component
public class AppUtils {

    @Autowired
    ContactUsRepository contacts;
    @Autowired
    GameRepository gameRepo;
    @Autowired
    GameTicketRepository gameticketRepo;
    @Autowired
    OrderRepository orderRepo;

    @Autowired
    PaymentResultsRepo paymentrepo;


    @Autowired
    SpringTemplateEngine templateEngine;



    @Autowired
    CustomElementFactoryImpl CustomElementFactoryImpl;


    @Value("${paynow.return.domain_url}")
    String domain;

    public ByteArrayOutputStream getTicketPdf(Long order_id, String uuid) throws WriterException, IOException {
        Order order = orderRepo.findByIdAndUuid(order_id, uuid);

        Context myContext = new Context();
        myContext.setVariable("uuid", uuid);
        myContext.setVariable("order_id", order_id);

        myContext.setVariable("order", order);
        myContext.setVariable("game", order.getTicket().getGame());
        myContext.setVariable("domain", domain);

        byte[] qrCodeBytes = QRCodeGenerator.generateQRCode2(order.getOrder_ticket_qr(), 250);
        myContext.setVariable("qr", Base64.getEncoder().encodeToString(qrCodeBytes));

        String htmlTemplate = templateEngine.process("ticket_for_download", myContext);
        Document document = Jsoup.parse(htmlTemplate, "UTF-8");
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ITextRenderer renderer = new ITextRenderer();

        SharedContext sharedContext = renderer.getSharedContext();
        sharedContext.setReplacedElementFactory(CustomElementFactoryImpl);

        sharedContext.setPrint(true);
        sharedContext.setInteractive(false);
        renderer.setDocumentFromString(document.toString(), "classpath:/static/");
        renderer.layout();
        renderer.createPDF(outputStream);
        return outputStream;
    }

}
