/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ticket.TicketSystem.api;

import com.ticket.TicketSystem.AppUtils;
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
import com.ticket.TicketSystem.PaymentService;
import com.ticket.TicketSystem.QRCodeGenerator;
import com.ticket.TicketSystem.SecureCodeGenerator;
import com.ticket.TicketSystem.entities.ContactUs;
import com.ticket.TicketSystem.entities.EmailDetails;
import com.ticket.TicketSystem.entities.Game;
import com.ticket.TicketSystem.entities.GameTicket;
import com.ticket.TicketSystem.entities.Order;
import com.ticket.TicketSystem.entities.PaymentResult;
import com.ticket.TicketSystem.entities.Ticket;
import com.ticket.TicketSystem.mail.EmailServiceImpl;
import com.ticket.TicketSystem.repositories.ContactUsRepository;
import com.ticket.TicketSystem.repositories.GameRepository;
import com.ticket.TicketSystem.repositories.GameTicketRepository;
import com.ticket.TicketSystem.repositories.OrderRepository;
import com.ticket.TicketSystem.repositories.PaymentResultsRepo;
import com.ticket.TicketSystem.AppUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.servlet.ModelAndView;
import zw.co.paynow.responses.StatusResponse;
import com.ticket.TicketSystem.repositories.TeamRepository;
import com.ticket.TicketSystem.repositories.TicketRepository;
import jakarta.mail.MessagingException;
import java.util.Base64;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Element;

/**
 *
 * @author Carron Muleya
 */
@RestController
@RequestMapping("/api")
public class ApiControllers {



    @Autowired
    AppUtils utils;

    @Autowired
    GameRepository gameRepo;
    @Autowired
    GameTicketRepository gameticketRepo;
    @Autowired
    OrderRepository orderRepo;

    @Autowired
    PaymentResultsRepo paymentrepo;

//    @Autowired
//    private ObjectToMapConvert objectToMapConvert;

    @Autowired
    ContactUsRepository contacts;
        
    @Autowired
    SpringTemplateEngine templateEngine;

    @Autowired
    EmailServiceImpl emailService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    CustomElementFactoryImpl CustomElementFactoryImpl;

    @Autowired
    PaymentService paymentService;

    @Value("${paynow.return.domain_url}")
    String domain;

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

    @GetMapping("/ticket_pdf/{order_id}/{uuid}")
    @ResponseBody
    public ResponseEntity getPdf(@PathVariable Long order_id, @PathVariable String uuid) throws WriterException, IOException {
        var outputStream = utils.getTicketPdf(order_id, uuid);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(outputStream.toByteArray());
    }

//    @GetMapping("/tickets")
//    public Iterable<Ticket> getTickets() {
//        return ticketRepo.findAll();
//    }
    @PostMapping(value = "payment/results/{order_id}/{uid}", consumes = {"application/x-www-form-urlencoded;charset=UTF-8", "application/x-www-form-urlencoded"})
    public ResponseEntity orderResults(@PathVariable Long order_id, @PathVariable String uid,
            @ModelAttribute PaymentResult paymentResult) throws WriterException, IOException, MessagingException {
        Order order = orderRepo.findByIdAndUuid(order_id, uid);
        if (order != null) {
            order.setPaid(paymentResult.isPaid());
            if (paymentResult.isPaid()) {
                GameTicket t=order.getTicket();
                t.setQuantity(t.getQuantity() - 1);
                gameticketRepo.save(t);
                ByteArrayOutputStream outputStream = utils.getTicketPdf(order_id, uid);
                emailService.sendEmailWithAttachmentFromByteArray(order.getEmail_address(),
                        "Bf Stadium ticket", "Please be advised that your ticket have been generated!",
                        outputStream, "Ticket_number_" + order.getId() + ".pdf");
            }
            PaymentResult order_results = order.getPaymentResult();
            if (order_results != null) {
                paymentResult.setId(order_results.getId());
            }
            paymentResult.setOrder(order);
            order.setPaymentResult(paymentResult);
            paymentrepo.save(paymentResult);
            orderRepo.save(order);

        } else {
            return ResponseEntity.ok("Order not found");
        }
        return ResponseEntity.ok("Thanks");
    }

    @GetMapping("/qr/{order_id}/{uid}")
    @ResponseBody
    public ResponseEntity<byte[]> getTicketQR3(@PathVariable Long order_id, @PathVariable String uid) throws WriterException, IOException {
        Order order = orderRepo.findByIdAndUuid(order_id, uid);
        if (order != null) {

            byte[] qrCodeBytes = QRCodeGenerator.generateQRCode(order.getOrder_ticket_qr(), 250);

            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCodeBytes);
        }
        return ResponseEntity.notFound().build();
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
        List<Order> orders=orderRepo.findByEmailAndGame(order.getEmail_address(),t.getGame().getId());
        if(orders.size()>=5){
             return ResponseEntity.badRequest().body("You have reached the maximum limit of purcasing order");
        }
        String code = SecureCodeGenerator.generateSecureCode(50);
        order.setOrder_ticket_qr(code);

        order.setUuid(UUID.randomUUID().toString());

        orderRepo.save(order);
        var method = order.getPayment_method();
        //Payment

        String redirectURL = paymentService.initPayment(order);

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
