/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ticket.TicketSystem.api;

import com.google.zxing.WriterException;
import com.ticket.TicketSystem.AppUtils;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ticket.TicketSystem.PaymentService;
import com.ticket.TicketSystem.entities.Game;
import com.ticket.TicketSystem.entities.GameTicket;
import com.ticket.TicketSystem.entities.Order;
import com.ticket.TicketSystem.entities.Ticket;
import com.ticket.TicketSystem.mail.EmailServiceImpl;
import com.ticket.TicketSystem.repositories.GameRepository;
import com.ticket.TicketSystem.repositories.GameTicketRepository;
import com.ticket.TicketSystem.repositories.OrderRepository;

import jakarta.servlet.http.HttpServletResponse;
import com.ticket.TicketSystem.repositories.TicketRepository;
import jakarta.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Carron Muleya
 */
@Controller
public class MvControllers {

    @Autowired
    GameRepository gameRepo;
    @Autowired
    GameTicketRepository gameticketsRepo;
    @Autowired
    AppUtils utils;

    @Autowired
    OrderRepository orderRepo;

    @Autowired
    PaymentService paymentService;

    @Autowired
    EmailServiceImpl emailService;

    @GetMapping("/book")
    public ModelAndView BookTicket(@RequestParam(required = true) int game, HttpServletResponse res) throws IOException {
        ModelAndView book = new ModelAndView();
        Game g = gameRepo.findById(game);
        if (g == null) {
            res.sendRedirect("/games");
            return null;
        }
//        for (GameTicket ticket : g.getTickets()) {
//            for (Order order : orderRepo.findByTicketAndGame(ticket.getId(), g.getId())) {
//                if (order.isPaid()) {
//                    ticket.setQuantity(ticket.getQuantity() - 1);
//                }
//            }
//        }

        book.addObject("game", g);
        book.setViewName("book");
        return book;
    }

    @GetMapping("/pay/{order_id}/{uuid}")
    public void toPay(@PathVariable Long order_id, @PathVariable String uuid, HttpServletResponse res) throws IOException {
        Order order = orderRepo.findByIdAndUuid(order_id, uuid);

        if (order == null) {
            ModelAndView m = new ModelAndView();
            m.setViewName("error");
            m.addObject("errorMsg", "Sorry, the order you are looking for does not exist or cannot be processed at the moment.");
            return;
        }

        String redirectURL = paymentService.initPayment(order);
        res.sendRedirect(redirectURL);
    }

    @GetMapping("/payment/return/{order_id}/{uuid}")
    public ModelAndView afterPayment(@PathVariable Long order_id, @PathVariable String uuid) {
        Order order = orderRepo.findByIdAndUuid(order_id, uuid);
        ModelAndView m = new ModelAndView();

        m.addObject("uuid", uuid);
        m.addObject("order_id", order_id);
        if (order == null) {

            m.setViewName("error");
            m.addObject("errorMsg", "Sorry, the order you are looking for does not exist or cannot be processed at the moment.");
            return m;
        }
        m.addObject("order", order);
        m.addObject("game", order.getTicket().getGame());
        if (order.isPaid()) {
            m.setViewName("view_ticket");
        } else {
            var paymentResult = paymentService.savePaymentResults(order, order.getPaymentResult().getPollurl());
            if (paymentResult.isPaid()) {
                GameTicket t=order.getTicket();
                t.setQuantity(t.getQuantity() - 1);
                gameticketRepo.save(t);
                ByteArrayOutputStream outputStream = utils.getTicketPdf(order_id, uid);
                emailService.sendEmailWithAttachmentFromByteArray(order.getEmail_address(),
                        "Bf Stadium ticket", "Please be advised that your ticket have been generated!",
                        outputStream, "Ticket_number_" + order.getId() + ".pdf");
                m.setViewName("view_ticket");
            } else {

                String payment_url = String.format("/pay/%d/%s", order_id, uuid);
                m.addObject("payment_url", payment_url);
                m.setViewName("order_not_paid");
            }
        }
        return m;
    }

    @GetMapping("/ticket/{order_id}/{uuid}")
    public ModelAndView viewTicket(@PathVariable Long order_id, @PathVariable String uuid) throws IOException, WriterException, MessagingException {
        Order order = orderRepo.findByIdAndUuid(order_id, uuid);
        ModelAndView m = new ModelAndView();

        m.addObject("uuid", uuid);
        m.addObject("order_id", order_id);
        if (order == null) {
            m.setViewName("error");
            m.addObject("errorMsg", "Sorry, the ticket you are looking for does not exist or cannot be processed at the moment.");
            return m;
        }
        m.addObject("order", order);
        m.addObject("game", order.getTicket().getGame());
        if (order.isPaid()) {
            m.setViewName("ticket");
        } else {
            var paymentResult = paymentService.savePaymentResults(order, uuid);
            if (paymentResult.isPaid()) {
                m.setViewName("ticket");
                ByteArrayOutputStream outputStream = utils.getTicketPdf(order_id, uuid);
                emailService.sendEmailWithAttachmentFromByteArray(order.getEmail_address(),
                        "Bf Stadium ticket", "Please be advised that your ticket have been generated!",
                        outputStream, "Ticket_number_" + order.getId() + ".pdf");
            } else {
                String payment_url = String.format("/pay/%d/%s", order_id, uuid);
                m.addObject("payment_url", payment_url);
                m.setViewName("order_not_paid");
            }
        }
        return m;
    }

    @GetMapping("/")
    public ModelAndView index(HttpServletResponse res) throws IOException {
        ModelAndView book = new ModelAndView();
        Iterable<Game> games = gameRepo.findByOrderByKickoffDesc(Limit.of(3));
        book.addObject("games", games);
        book.setViewName("index");
        return book;
    }

    @GetMapping("/games")
    public ModelAndView viewGames(HttpServletResponse res) throws IOException {
        ModelAndView book = new ModelAndView();
        Iterable<Game> games = gameRepo.findAll();

        book.addObject("games", games);
        book.setViewName("games");
        return book;
    }

    @GetMapping("/checkout")
    public String checkOut(@RequestParam(required = true) int game, @RequestParam(required = true) String ticket, HttpServletResponse res, Model model) throws IOException {
        Game g = gameRepo.findById(game);
        if (g == null) {
            res.sendRedirect("/games");
            return null;
        }
//        List<GameTicket> tickets = g.getTickets();
        GameTicket t = gameticketsRepo.findByTypeAndGameIdIgnoreCase(ticket, g);
//        for (GameTicket ticket : tickets) {
//            if (ticket.getTicket().getType().equals(ticket_name)) { // Example condition
//                t = ticket;
//                break; // Exit the loop once the ticket is found
//            }
//        }

        if (t == null) {
            System.out.println("Ticket Not found");
            res.sendRedirect("/games.html");
            return null;
        }

        model.addAttribute("game", g);
        model.addAttribute("ticket", t);
        return "checkout";
    }
}
