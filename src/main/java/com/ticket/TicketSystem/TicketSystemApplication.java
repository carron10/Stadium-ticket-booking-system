package com.ticket.TicketSystem;

import com.ticket.TicketSystem.entities.Game;
import com.ticket.TicketSystem.repositories.GameRepository;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class TicketSystemApplication implements CommandLineRunner {

    
    @Autowired
    GameRepository gameRepository;
    
    
    public static void main(String[] args) {
        SpringApplication.run(TicketSystemApplication.class, args);
    }

    @GetMapping("/games")
    public Iterable<Game> Games() {
        return gameRepository.findAll();
    }

    @Override
    public void run(String... args) throws Exception {
        //delete existing games
        gameRepository.deleteAll();
        
        Game game=new Game();
        game.setAway_team("ChickenInn FC");
        game.setHome_team("Highlanders FC");
        
        Date kickoff=new Date();
        kickoff.setDate(14);
        kickoff.setHours(2);
        kickoff.setMinutes(30);
        kickoff.setMonth(2);
        kickoff.setYear(2024);
        game.setKickoff(kickoff);
        
        game.setAway_team_logo("/images/chickenInnLogo.jpg");
        game.setHome_team_logo("/images/chickenInnLogo.png");
        gameRepository.save(game);
    }

}

/**
 * Plan Tables 1. Tickets[to store tickets prices,types and the number
 * available] 2. User[to store usernames, addresses and etc] 3. UserMeta [To
 * store other details for users] 3. Payments [to store tokens,and payment
 * details] 4. Games [To store games, and their due dates] 5. Orders [to store
 * ticket orders] 6. OrderMeta [To store meta data about an order]
 */
