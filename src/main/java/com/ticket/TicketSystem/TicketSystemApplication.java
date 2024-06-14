package com.ticket.TicketSystem;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

import com.ticket.TicketSystem.entities.Game;
import com.ticket.TicketSystem.entities.Ticket;
import com.ticket.TicketSystem.repositories.GameRepository;
import com.ticket.TicketSystem.repositories.TeamRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@SpringBootApplication
@RestController
public class TicketSystemApplication implements CommandLineRunner {

    @Autowired
    GameRepository gameRepo;
    @Autowired
    TeamRepository ticketsRepo;

    public static void main(String[] args) {
        SpringApplication.run(TicketSystemApplication.class, args);
    }
    
     @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Override
    public void run(String... args) throws Exception {
        //delete existing games
        // gameRepo.deleteAll();

        // for (int i = 0; i < 10; i++) {
        //     Game game = new Game();
        //     game.setAway_team("ChickenInn FC");
        //     game.setHome_team("Highlanders FC");

        //     SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        //     Date kickoff = formatter.parse("09-03-2024 15:00");
        //     game.setKickoff(kickoff);
        //     game.setType("Premier League");
        //     game.setFeature_img("images/stadium lawn.jpg");
        //     game.setAway_team_logo("/images/chickenInnLogo.jpg");
        //     game.setHome_team_logo("/images/chickenInnLogo.png");
        //     gameRepo.save(game);

        //     Ticket t1 = new Ticket();
        //     t1.setGame(game);
        //     t1.setQuantity(4);
        //     t1.setType("VIP Reserved Side");
        //     t1.setPrice(2);

        //     t1.setDescription("Ticket Descption");
        //     ticketsRepo.save(t1);

        //     Ticket t2 = new Ticket();
        //     t2.setGame(game);
        //     t2.setQuantity(25);
        //     t2.setPrice(3);
        //     t2.setType("VVIP Side");
        //     t2.setDescription("Ticket Descption");
        //     ticketsRepo.save(t2);

        //     Ticket t3 = new Ticket();
        //     t3.setGame(game);
        //     t3.setQuantity(5);
        //     t3.setPrice(5);
        //     t3.setType("VVIP Center");
        //     t3.setDescription("Ticket Descption");
        //     ticketsRepo.save(t3);

        //     Ticket t4 = new Ticket();
        //     t4.setGame(game);
        //     t4.setQuantity(3);
        //     t4.setType("Empakweni");
        //     t4.setPrice(6);
        //     t4.setDescription("Ticket for home team fans");
        //     ticketsRepo.save(t4);

        //     Ticket t5 = new Ticket();
        //     t5.setGame(game);
        //     t5.setQuantity(20);
        //     t5.setPrice(5);
        //     t5.setType("Mpilo End");
        //     t5.setDescription("Ticket for away team fans");
        //     ticketsRepo.save(t5);

        //     Ticket t6 = new Ticket();
        //     t6.setGame(game);
        //     t6.setQuantity(18);
        //     t6.setPrice(5);
        //     t6.setType("Soweto");
        //     t6.setDescription("The die hard home fans area");
        //     ticketsRepo.save(t6);
        // }

    }

}

/**
 * Plan Tables 1. Tickets[to store tickets prices,types and the number
 * available] 2. User[to store usernames, addresses and etc] 3. UserMeta [To
 * store other details for users] 3. Payments [to store tokens,and payment
 * details] 4. Games [To store games, and their due dates] 5. Orders [to store
 * ticket orders] 6. OrderMeta [To store meta data about an order]
 */
