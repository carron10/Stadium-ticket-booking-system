/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ticket.TicketSystem.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.google.zxing.WriterException;
import com.ticket.TicketSystem.CustomElementFactoryImpl;
import com.ticket.TicketSystem.ObjectToMapConvert;
import com.ticket.TicketSystem.PaymentService;
import com.ticket.TicketSystem.QRCodeGenerator;
import com.ticket.TicketSystem.SecureCodeGenerator;
import com.ticket.TicketSystem.entities.ContactUs;
import com.ticket.TicketSystem.entities.Game;
import com.ticket.TicketSystem.entities.GameTicket;
import com.ticket.TicketSystem.entities.Order;
import com.ticket.TicketSystem.entities.PaymentResult;
import com.ticket.TicketSystem.entities.Ticket;
import com.ticket.TicketSystem.repositories.ContactUsRepository;
import com.ticket.TicketSystem.repositories.GameRepository;
import com.ticket.TicketSystem.repositories.GameTicketRepository;
import com.ticket.TicketSystem.repositories.OrderRepository;
import com.ticket.TicketSystem.repositories.PaymentResultsRepo;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.servlet.ModelAndView;
import zw.co.paynow.responses.StatusResponse;
import com.ticket.TicketSystem.repositories.TeamRepository;
import com.ticket.TicketSystem.repositories.TicketRepository;

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
    GameRepository gameRepo;
    @Autowired
    GameTicketRepository gameticketRepo;
    @Autowired
    OrderRepository orderRepo;
    
    @Autowired
    PaymentResultsRepo paymentrepo;
    
    @Autowired
    private ObjectToMapConvert objectToMapConvert;

    
    @Autowired
    SpringTemplateEngine templateEngine;
    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    CustomElementFactoryImpl CustomElementFactoryImpl;
    
    @Autowired
    PaymentService paymentService;

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
        return gameRepo.findAll();
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

//    @GetMapping("/tickets")
//    public Iterable<Ticket> getTickets() {
//        return ticketRepo.findAll();
//    }
    
    
    
    @PostMapping(value="payment/results/{order_id}/{uid}",consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public ResponseEntity placeOrder(@PathVariable Long order_id,@PathVariable String uid,
        @ModelAttribute PaymentResult paymentResult){
        Order order=orderRepo.findByIdAndUuid(order_id, uid);
        if(order!=null){
           
            StatusResponse status=new StatusResponse( ObjectToMapConvert.convertToMap(paymentResult));
            order.setPaid(status.paid());
            paymentResult.setOrder(order);
            order.setPaymentResult(paymentResult);
            paymentrepo.save(paymentResult);
            orderRepo.save(order);
            
        }
        return ResponseEntity.ok("Thanks");
    }
    
    

    @PostMapping(value = "/place_order", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseBody
    public ResponseEntity placeOrder(@ModelAttribute @Valid Order order, HttpServletResponse res) throws WriterException, IOException {
        if (order.getTicket() == null) {
            return ResponseEntity.badRequest().body("error no ticket specified");
        }

        GameTicket t = order.getTicket();
        if (t.getQuantity() == 0) {
            return ResponseEntity.badRequest().body("No more tickets available in stock");
        }
        Game g = t.getGame();
        t.setQuantity(t.getQuantity() - 1);
        gameticketRepo.save(t);

        String code = SecureCodeGenerator.generateSecureCode(50);
        order.setOrder_ticket_qr(code);
        
        order.setUuid(UUID.randomUUID().toString());
        
        orderRepo.save(order);
        var method = order.getPayment_method();
        //Payment
        
         String redirectURL=paymentService.initPayment(order);
         res.sendRedirect(redirectURL);
        return ResponseEntity.ok("Done");
    }

//    @GetMapping("/ticket")
//    public Ticket getTicketByGameAndType(@RequestParam(required = true) int game, @RequestParam(required = true) String ticket) {
//        Game g = gameRepo.findById(game);
//        Ticket t = gameticketRepo.findByTypeAndGameIdIgnoreCase(ticket, g);
//        return t;
//    }

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
