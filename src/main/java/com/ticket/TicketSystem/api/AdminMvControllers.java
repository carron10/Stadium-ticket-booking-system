/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ticket.TicketSystem.api;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ticket.TicketSystem.PaymentService;
import com.ticket.TicketSystem.entities.Game;
import com.ticket.TicketSystem.entities.Team;
import com.ticket.TicketSystem.entities.Ticket;
import com.ticket.TicketSystem.repositories.GameRepository;
import com.ticket.TicketSystem.repositories.OrderRepository;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ticket.TicketSystem.repositories.TeamRepository;
import com.ticket.TicketSystem.repositories.TicketRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Carron Muleya
 */
@Controller
@RequestMapping("/admin")
public class AdminMvControllers {

    @Autowired
    GameRepository gameRepo;

    @Autowired
    TicketRepository ticketsRepo;

    @Autowired
    TeamRepository teamsRepo;

    @Autowired
    OrderRepository orderRepo;

    @Autowired
    PaymentService paymentService;

    @GetMapping({"", "/"})
    public String index(HttpServletResponse res, HttpServletRequest request) throws IOException {
        
        if (request.getRequestURI().equals("/admin")) {
            return "redirect:/admin/"; 
        }
        ModelAndView book = new ModelAndView();
        Iterable<Game> games = gameRepo.findByOrderByKickoffDesc(Limit.of(3));
        book.addObject("games", games);
        book.setViewName("admin/index");
        return "admin/index";
    }

    @GetMapping("/games")

    public ModelAndView games(HttpServletResponse res) throws IOException {
        ModelAndView book = new ModelAndView();
        Iterable<Game> games = gameRepo.findAll();
        Iterable<Ticket> tickets = ticketsRepo.findAll();
        book.addObject("tickets", tickets);
        book.addObject("games", games);
        book.setViewName("admin/games");
        return book;
    }

    @GetMapping("/game")
    public ModelAndView games(@RequestParam Long id) throws IOException {
        ModelAndView book = new ModelAndView();
        Optional<Game> game = gameRepo.findById(id);
        Iterable<Team> teams = teamsRepo.findAll();
        book.addObject("teams", teams);
        book.addObject("game", game.get());
        book.setViewName("admin/view_game");
        return book;
    }

    @GetMapping("/new_game")
    public ModelAndView addNewGame(HttpServletResponse res) throws IOException {
        ModelAndView book = new ModelAndView();
        Iterable<Team> teams = teamsRepo.findAll();
        book.addObject("teams", teams);
        Iterable<Ticket> tickets = ticketsRepo.findAll();
        book.addObject("tickets", tickets);
        book.setViewName("admin/new_game");
        return book;
    }

    @GetMapping("/tickets")
    public ModelAndView ticket(HttpServletResponse res) throws IOException {
        ModelAndView book = new ModelAndView();

        Iterable<Ticket> tickets = ticketsRepo.findAll();
        book.addObject("tickets", tickets);
        book.setViewName("admin/tickets");
        return book;
    }

    @GetMapping("/teams")
    public ModelAndView teams(HttpServletResponse res) throws IOException {
        ModelAndView book = new ModelAndView();
        Iterable<Team> teams = teamsRepo.findAll();
        book.addObject("teams", teams);
        book.setViewName("admin/teams");
        return book;
    }

}
