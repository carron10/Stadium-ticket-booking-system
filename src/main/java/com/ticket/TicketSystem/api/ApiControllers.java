/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ticket.TicketSystem.api;

import com.google.zxing.WriterException;
import com.lowagie.text.Image;
import com.openhtmltopdf.resource.Resource;
import com.ticket.TicketSystem.CustomElementFactoryImpl;
import com.ticket.TicketSystem.QRCodeGenerator;
import com.ticket.TicketSystem.SecureCodeGenerator;
import com.ticket.TicketSystem.entities.ContactUs;
import com.ticket.TicketSystem.entities.Game;
import com.ticket.TicketSystem.entities.Order;
import com.ticket.TicketSystem.entities.Ticket;
import com.ticket.TicketSystem.repositories.ContactUsRepository;
import com.ticket.TicketSystem.repositories.GameRepository;
import com.ticket.TicketSystem.repositories.OrderRepository;
import com.ticket.TicketSystem.repositories.TicketRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import static org.jsoup.nodes.Entities.EscapeMode.xhtml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.ITextUserAgent;
import org.xhtmlrenderer.render.BlockBox;
import zw.co.paynow.core.Payment;
import zw.co.paynow.core.Paynow;
import zw.co.paynow.responses.StatusResponse;
import zw.co.paynow.responses.WebInitResponse;

/**
 *
 * @author Carron Muleya
 */
@RestController
@RequestMapping("/api")
public class ApiControllers {

    @Autowired
    ContactUsRepository contacts;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    TicketRepository ticketRepo;
    @Autowired
    OrderRepository orderRepo;
    @Autowired
    SpringTemplateEngine templateEngine;
    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    CustomElementFactoryImpl CustomElementFactoryImpl;

    public String getStaticResourcesFolderPath() {
        String resourcePath = "static/"; // replace with the path to your static resources folder
        org.springframework.core.io.Resource resource = resourceLoader.getResource(resourcePath);
        try {
            return resource.getFile().getAbsolutePath(); // Handle the exception or return a default value
        } catch (IOException ex) {
            Logger.getLogger(ApiControllers.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @GetMapping("/games")
    @ResponseBody
    public Iterable<Game> getGames() {
        return gameRepository.findAll();
    }

    @PostMapping("/qr2")
    @ResponseBody
    public ResponseEntity<byte[]> getQR2(@RequestBody String body) throws WriterException, IOException {
        byte[] qrCodeBytes = QRCodeGenerator.generateQRCode2(body, 250);

        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCodeBytes);
    }

    @PostMapping("/qr")
    @ResponseBody
    public ResponseEntity<byte[]> getQR(@RequestBody String body) throws WriterException, IOException {
        byte[] qrCodeBytes = QRCodeGenerator.generateQRCode(body, 250);

        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCodeBytes);
    }

    @GetMapping("/qr")
    @ResponseBody
    public ResponseEntity<byte[]> getQR3() throws WriterException, IOException {
        byte[] qrCodeBytes = QRCodeGenerator.generateQRCode("Hello world", 250);

        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCodeBytes);
    }

    @GetMapping("/pdf")
    @ResponseBody
    public ResponseEntity getPdf() throws WriterException, IOException {
//        byte[] qrCodeBytes=QRCodeGenerator.generateQRCode(body, 250);
        System.out.println(getStaticResourcesFolderPath() + "\\file");

        Context myContext = new Context();

//        myContext.setVariables();
        String htmlTemplate = templateEngine.process("ticket", myContext);
        Document document = Jsoup.parse(htmlTemplate, "UTF-8");
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

        String baseUrl = FileSystems.getDefault().getPath(getStaticResourcesFolderPath() + "\\file").toUri().toURL().toString();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ITextRenderer renderer = new ITextRenderer();

        SharedContext sharedContext = renderer.getSharedContext();
        sharedContext.setReplacedElementFactory(CustomElementFactoryImpl);
        sharedContext.setPrint(true);
        sharedContext.setInteractive(false);
        renderer.setDocumentFromString(document.toString(), "classpath:/static/");
        renderer.layout();
        renderer.createPDF(outputStream);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(outputStream.toByteArray());
    }

    @GetMapping("/tickets")
    public Iterable<Ticket> getTickets() {
        return ticketRepo.findAll();
    }
    
    @PostMapping("payment/results/{order_id}/{uid}")
    public ResponseEntity placeOrder(@PathVariable int order_id,@PathVariable String uid,@RequestBody String body){
        System.out.println(body);
        return ResponseEntity.ok("Thanks");
        
    }

    @PostMapping(value = "/place_order", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseBody
    public ResponseEntity placeOrder(@ModelAttribute @Valid Order order, HttpServletResponse res) throws WriterException, IOException {
        if (order.getTicket() == null) {
            return ResponseEntity.badRequest().body("error no ticket specified");
        }

        Ticket t = order.getTicket();
        if (t.getQuantity() == 0) {
            return ResponseEntity.badRequest().body("No more tickets available in stock");
        }
        Game g = t.getGame();
        t.setQuantity(t.getQuantity() - 1);
        ticketRepo.save(t);

        String code = SecureCodeGenerator.generateSecureCode(50);
        order.setOrder_ticket_qr(code);
        
        order.setUuid(UUID.randomUUID().toString());
        
        orderRepo.save(order);
        var method = order.getPayment_method();
        //Payment
        Paynow paynow = new Paynow("14899", "c634cc29-f807-431d-a02b-3fc534ac1921");
        paynow.setReturnUrl("https://bfticketbokkingsystem.azurewebsites.net/payment/return/" + order.getId() + "/" + order.getUuid());
        paynow.setResultUrl("https://bfticketbokkingsystem.azurewebsites.net/api/payment/results/" + order.getId() + "/" + order.getUuid());

        Payment payment = paynow.createPayment("Invoice " + order.getId());

        // Passing in the name of the item and the price of the item
        payment.add("Tickect", t.getPrice());
//        payment.add("Apples", 3.4);
        //Initiating the transaction
        WebInitResponse response = paynow.send(payment);
        //If a mobile transaction,
        //MobileInitResponse response = paynow.sendMobile(payment, "0771234567", MobileMoneyMethod.ECOCASH);

        if (response.isRequestSuccess()) {
            // Get the url to redirect the user to so they can make payment
            String redirectURL = response.redirectURL();
            // Get the poll url of the transaction
            String pollUrl = response.pollUrl();

            //checking if the payment has been paid
            StatusResponse status = paynow.pollTransaction(pollUrl);

            System.out.println(pollUrl);
            System.out.println("Status: " + status.paid());

            res.sendRedirect(redirectURL);
            if (status.paid()) {
                // Yay! Transaction was paid for
            } else {
                System.out.println("Why you no pay?");
            }

        } else {
            // Something went wrong
            System.out.println(response.errors());
        }
        return ResponseEntity.ok("Done");
    }

    @GetMapping("/ticket")
    public Ticket getTicketByGameAndType(@RequestParam(required = true) int game, @RequestParam(required = true) String ticket) {
        Game g = gameRepository.findById(game);
        Ticket t = ticketRepo.findByTypeAndGameIdIgnoreCase(ticket, g);
        return t;
    }

    //http://localhost:8080/contact
    @PostMapping(value = "/contact", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseBody
    public ResponseEntity contactUs(@ModelAttribute ContactUs contact) {
        //Save the contact infor
        try {
            contacts.save(contact);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Could Not save you message");
        }

        return ResponseEntity.ok(contact);
    }

//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
//        Map<String,String> errors=new HashMap();
//        BindingResult result=ex.getBindingResult();
//        for(FieldError error : result.getFieldErrors()){
//            errors.put(error.getField(), error.getDefaultMessage());
//        }
//        return ResponseEntity.badRequest().body(errors);
//    }
}
