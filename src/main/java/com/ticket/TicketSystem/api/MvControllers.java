/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ticket.TicketSystem.api;

import com.ticket.TicketSystem.entities.Game;
import com.ticket.TicketSystem.entities.Ticket;
import com.ticket.TicketSystem.repositories.GameRepository;
import com.ticket.TicketSystem.repositories.TicketRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Carron Muleya
 */
@Controller
public class MvControllers {

    @Autowired
    GameRepository gameRepository;
    @Autowired
    TicketRepository ticketsRepo;

    @GetMapping("/book")
    public ModelAndView BookTicket(@RequestParam(required = true) int game, HttpServletResponse res) throws IOException {
        ModelAndView book = new ModelAndView();
        Game g = gameRepository.findById(game);
        if (g == null) {
            res.sendRedirect("/games.html");
            return null;
        }
        book.addObject("game", g);
        book.setViewName("book");
        return book;
    }
    
    @GetMapping("/payment/return/{order_id}/{uid}")
    public String afterPayment( @PathVariable int order_id,@PathVariable String uid){
        return "ticket";
    }

    @GetMapping("/ticket")
    public String getTicket() {
        return "ticket";
    }
    @GetMapping("/")
    public ModelAndView index(HttpServletResponse res) throws IOException {
        ModelAndView book = new ModelAndView();
        Iterable<Game> games = gameRepository.findByOrderByKickoffDesc(Limit.of(3));
        book.addObject("games", games);
        book.setViewName("index");
        return book;
    }

    @GetMapping("/games")
    public ModelAndView viewGames(HttpServletResponse res) throws IOException {
        ModelAndView book = new ModelAndView();
        Iterable<Game> games = gameRepository.findAll();

        book.addObject("games", games);
        book.setViewName("games");
        return book;
    }

    @GetMapping("/checkout")
    public String checkOut(@RequestParam(required = true) int game, @RequestParam(required = true) String ticket, HttpServletResponse res, Model model) throws IOException {
        Game g = gameRepository.findById(game);
        if (g == null) {
            res.sendRedirect("/games.html");
            return null;
        }
        Ticket t = ticketsRepo.findByTypeAndGameIdIgnoreCase(ticket, g);

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
