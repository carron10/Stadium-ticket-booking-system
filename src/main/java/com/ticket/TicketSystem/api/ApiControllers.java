/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ticket.TicketSystem.api;

import com.google.zxing.WriterException;
import com.ticket.TicketSystem.QRCodeGenerator;
import com.ticket.TicketSystem.entities.ContactUs;
import com.ticket.TicketSystem.entities.Game;
import com.ticket.TicketSystem.entities.Order;
import com.ticket.TicketSystem.entities.Ticket;
import com.ticket.TicketSystem.repositories.ContactUsRepository;
import com.ticket.TicketSystem.repositories.GameRepository;
import com.ticket.TicketSystem.repositories.OrderRepository;
import com.ticket.TicketSystem.repositories.TicketRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *
 * @author Carron Muleya
 */
@RestController
@RequestMapping("/api")
public class ApiControllers{

    @Autowired
    ContactUsRepository contacts;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    TicketRepository ticketRepo;
    @Autowired
    OrderRepository orderRepo;

    @GetMapping("/games")
    @ResponseBody
    public Iterable<Game> getGames() {
        return gameRepository.findAll();
    }
    
     @GetMapping("/qr")
    @ResponseBody
    public ResponseEntity<byte[]> getQR() throws WriterException {
        byte[] qrCodeBytes=QRCodeGenerator.generateQRCode("hello world", 250, 250);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCodeBytes);
    }

    @GetMapping("/tickets")
    public Iterable<Ticket> getTickets() {
        return ticketRepo.findAll();
    }

    @PostMapping(value = "/place_order", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseBody
    public ResponseEntity placeOrder(@ModelAttribute @Valid Order order) {
        if(order.getTicket()==null){
            return ResponseEntity.badRequest().body("error no ticket specified");
        }
        
        Ticket t=order.getTicket();
        if(t.getQuantity()==0){
             return ResponseEntity.badRequest().body("No more tickets available in stock");
        }
        Game g=t.getGame();
        t.setQuantity(t.getQuantity()-1);
        ticketRepo.save(t);
        orderRepo.save(order);
        return ResponseEntity.ok(order);
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
