package com.ticket.TicketSystem;

import com.ticket.TicketSystem.entities.Game;
import com.ticket.TicketSystem.entities.Ticket;
import com.ticket.TicketSystem.repositories.GameRepository;
import com.ticket.TicketSystem.repositories.TicketRepository;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class TicketSystemApplication implements CommandLineRunner {

    @Autowired
    GameRepository gameRepository;
    @Autowired
    TicketRepository ticketsRepo;

    public static void main(String[] args) {
        SpringApplication.run(TicketSystemApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //delete existing games
        gameRepository.deleteAll();

        for (int i = 0; i < 10; i++) {
            Game game = new Game();
            game.setAway_team("ChickenInn FC");
            game.setHome_team("Highlanders FC");

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");

            Date kickoff = formatter.parse("09-03-2024 15:00");
//            System.out.println("Date :::" + kickoff);
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
//            LocalDate kickoff = LocalDate.parse("09-03-2024 02:00 PM", formatter);

//            ZoneId zoneId=ZoneId.of("UTC");
//            System.out.println("Date :::" + kickoff);
//            Instant instant=kickoff.atStartOfDay(zoneId).toInstant();
            game.setKickoff(kickoff);
            game.setType("Premier League");
            game.setFeature_img("images/stadium lawn.jpg");
            game.setAway_team_logo("/images/chickenInnLogo.jpg");
            game.setHome_team_logo("/images/chickenInnLogo.png");
            gameRepository.save(game);

            Ticket t1 = new Ticket();
            t1.setGame(game);
            t1.setQuantity(20);
            t1.setType("VIP Reserved Side");
            t1.setPrice(20);

            t1.setDescription("Ticket Descption");
            ticketsRepo.save(t1);

            Ticket t2 = new Ticket();
            t2.setGame(game);
            t2.setQuantity(20);
            t2.setPrice(15);
            t2.setType("VVIP Side");
            t2.setDescription("Ticket Descption");
            ticketsRepo.save(t2);

            Ticket t3 = new Ticket();
            t3.setGame(game);
            t3.setQuantity(20);
            t3.setPrice(10);
            t3.setType("VVIP Center");
            t3.setDescription("Ticket Descption");
            ticketsRepo.save(t3);

            Ticket t4 = new Ticket();
            t4.setGame(game);
            t4.setQuantity(20);
            t4.setType("Empakweni");
            t4.setPrice(5);
            t4.setDescription("Ticket for home team fans");
            ticketsRepo.save(t4);

            Ticket t5 = new Ticket();
            t5.setGame(game);
            t5.setQuantity(20);
            t5.setPrice(5);
            t5.setType("Mpilo End");
            t5.setDescription("Ticket for away team fans");
            ticketsRepo.save(t5);

            Ticket t6 = new Ticket();
            t6.setGame(game);
            t6.setQuantity(20);
            t6.setPrice(5);
            t6.setType("Soweto");
            t6.setDescription("The die hard home fans area");
            ticketsRepo.save(t6);
        }

    }

}

/**
 * Plan Tables 1. Tickets[to store tickets prices,types and the number
 * available] 2. User[to store usernames, addresses and etc] 3. UserMeta [To
 * store other details for users] 3. Payments [to store tokens,and payment
 * details] 4. Games [To store games, and their due dates] 5. Orders [to store
 * ticket orders] 6. OrderMeta [To store meta data about an order]
 */
