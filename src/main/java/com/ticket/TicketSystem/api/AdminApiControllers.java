/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ticket.TicketSystem.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.ticket.TicketSystem.GlobalExceptionHandler;
import com.ticket.TicketSystem.GlobalExceptionHandler.ErrorResponse;
import com.ticket.TicketSystem.ObjectToMapConvert;
import com.ticket.TicketSystem.PaymentService;
import com.ticket.TicketSystem.QRCodeGenerator;
import com.ticket.TicketSystem.entities.Game;
import com.ticket.TicketSystem.entities.GameTicket;
import com.ticket.TicketSystem.entities.Team;
import com.ticket.TicketSystem.entities.Ticket;
import com.ticket.TicketSystem.repositories.ContactUsRepository;
import com.ticket.TicketSystem.repositories.GameRepository;
import com.ticket.TicketSystem.repositories.GameTicketRepository;
import com.ticket.TicketSystem.repositories.OrderRepository;
import com.ticket.TicketSystem.repositories.PaymentResultsRepo;

import jakarta.validation.Valid;
import com.ticket.TicketSystem.repositories.TeamRepository;
import com.ticket.TicketSystem.repositories.TicketRepository;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Carron Muleya
 */
@RestController
@RequestMapping("/admin/api")
public class AdminApiControllers {

    @Autowired
    ContactUsRepository contacts;
    @Autowired
    GameRepository gamesRepo;

    @Autowired
    GameTicketRepository gameticketRepo;

    @Autowired
    TicketRepository ticketsRepo;

    @Value("${media.upload.dir}")
    String uploading_dir;

    @Autowired
    TeamRepository teamsRepo;

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
        return gamesRepo.findAll();
    }

    @PutMapping(value = "/games")
    public ResponseEntity<Object> updateGame(@RequestParam(value = "featured_img", required = false) MultipartFile featured_img,
            @RequestParam("game_id") Long game_id,
            @RequestParam("away_team") Long away_team_id,
            @RequestParam("home_team") Long home_team_id,
            @RequestParam("name") String name,
            @RequestParam("kickoff") String kickoffDate,
            @RequestParam("Tickets") String tickets) throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date kickoff = dateFormat.parse(kickoffDate);

        ObjectMapper objectMapper = new ObjectMapper();

        List<Map<String, Object>> jsonArray = objectMapper.readValue(tickets, new TypeReference<List<Map<String, Object>>>() {
        });
        Optional<Game> get_game = gamesRepo.findById(game_id);

        if (get_game.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("A Game can not be made of one team!!"));
        }

        Game game = get_game.get();

        List<GameTicket> gametickets = new LinkedList();
        // Process each item in the jsonArray
        for (Map<String, Object> item : jsonArray) {
            // Access properties of each item
            Integer included = (Integer) item.get("included"); // Assuming 'included' is an Integer
            System.out.println(item.get("price"));
            double price = ((Number) item.get("price")).doubleValue();
            Integer quantity = (Integer) item.get("quantity");

            Long ticket_id = null;
            if (item.get("ticket_id") instanceof Integer integer) {
                ticket_id = integer.longValue();
            } else if (item.get("ticket_id") instanceof Long long1) {
                ticket_id = long1;
            }

            Optional<GameTicket> ticket = gameticketRepo.findById(ticket_id);
            if (ticket.isEmpty()) {
                throw new Exception("Ticket  ID specified is null");
            }
            GameTicket gameTicket = ticket.get();
            gameTicket.setPrice((float) price);
            gameTicket.setQuantity(quantity);
            gametickets.add(gameTicket);
        }

        game.setName(name);
        game.setKickoff(kickoff);
        game.setTickets(gametickets);
        Optional<Team> away_team = teamsRepo.findById(away_team_id);
        Optional<Team> home_team = teamsRepo.findById(home_team_id);

        if (Objects.equals(away_team_id, home_team_id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("A Game can not be made of one team!!"));
        }
        game.setAway_team(away_team.get());
        game.setHome_team(home_team.get());

        // Process the logo file if it's provided
        if (featured_img != null && !featured_img.isEmpty()) {
            try {
                // Save the logo file and set its URL to the team object
                String fileName = StringUtils.cleanPath(featured_img.getOriginalFilename());
                Path uploadPath = Paths.get(uploading_dir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream inputStream = featured_img.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    game.setFeature_img("/uploads/" + fileName); // Set the URL to access the uploaded logo
                }
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload featured_img");
            }
        }

        // Save the team object to the database
        gamesRepo.save(game);
        gameticketRepo.saveAll(gametickets);

        return ResponseEntity.ok("Game saved successfully!!");
    }

    @PostMapping(value = "/games")
    public ResponseEntity<Object> addGame(@RequestParam(value = "featured_img", required = false) MultipartFile featured_img,
            @RequestParam("away_team") Long away_team_id,
            @RequestParam("home_team") Long home_team_id,
            @RequestParam("name") String name,
            @RequestParam("kickoff") String kickoffDate,
            @RequestParam("Tickets") String tickets) throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date kickoff = dateFormat.parse(kickoffDate);

        ObjectMapper objectMapper = new ObjectMapper();

        List<Map<String, Object>> jsonArray = objectMapper.readValue(tickets, new TypeReference<List<Map<String, Object>>>() {
        });
        Game game = new Game();
        List<GameTicket> gametickets = new LinkedList();
        // Process each item in the jsonArray
        for (Map<String, Object> item : jsonArray) {
            // Access properties of each item
            Integer included = (Integer) item.get("included"); // Assuming 'included' is an Integer
            System.out.println(item.get("price"));
            double price = ((Number) item.get("price")).doubleValue();
            Integer quantity = (Integer) item.get("quantity");

            Long ticket_id = null;
            if (item.get("ticket_id") instanceof Integer) {
                ticket_id = ((Integer) item.get("ticket_id")).longValue();
            } else if (item.get("ticket_id") instanceof Long) {
                ticket_id = (Long) item.get("ticket_id");
            }

            Optional<Ticket> ticket = ticketsRepo.findById(ticket_id);
            if (ticket.isEmpty()) {
                throw new Exception("Ticket  ID specified is null");
            }
            var t = ticket.get();
            GameTicket gameTicket = new GameTicket();
            gameTicket.setGame(game);

            gameTicket.setTicket(t);
            gameTicket.setPrice((float) price);
            gameTicket.setQuantity(quantity);
            gametickets.add(gameTicket);
        }

        game.setName(name);
        game.setKickoff(kickoff);
        game.setTickets(gametickets);
        Optional<Team> away_team = teamsRepo.findById(away_team_id);
        Optional<Team> home_team = teamsRepo.findById(home_team_id);

        if (Objects.equals(away_team_id, home_team_id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("A Game can not be made of one team!!"));
        }
        game.setAway_team(away_team.get());
        game.setHome_team(home_team.get());

        // Process the logo file if it's provided
        if (featured_img != null && !featured_img.isEmpty()) {
            try {
                // Save the logo file and set its URL to the team object
                String fileName = StringUtils.cleanPath(featured_img.getOriginalFilename());
                Path uploadPath = Paths.get(uploading_dir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream inputStream = featured_img.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    game.setFeature_img("/uploads/" + fileName); // Set the URL to access the uploaded logo
                }
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload featured_img");
            }
        }

        // Save the team object to the database
        Game savedGame = gamesRepo.save(game);
        gameticketRepo.saveAll(gametickets);

        return ResponseEntity.ok("Game saved successfully!!");
    }

    @PostMapping(value = "/teams")
    public ResponseEntity<Object> addTeam(@RequestParam(value = "logo", required = false) MultipartFile logoFile,
            @RequestParam("name") String name, @RequestParam(required = false) String logo_url) {

        // Create a new Team object
        Team team = new Team();
        team.setName(name);

        if (logo_url != null) {
            team.setLogo(logo_url);
        } else if (logoFile != null && !logoFile.isEmpty()) {
            try {
                // Save the logo file and set its URL to the team object
                String fileName = StringUtils.cleanPath(logoFile.getOriginalFilename());
                Path uploadPath = Paths.get(uploading_dir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream inputStream = logoFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    team.setLogo("/uploads/" + fileName); // Set the URL to access the uploaded logo
                }
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload logo");
            }
        }

        // Save the team object to the database
        Team savedTeam = teamsRepo.save(team);
        return ResponseEntity.ok(savedTeam);
    }

    @DeleteMapping(value = "/games/{id}")
    public ResponseEntity<Object> deleteGame(@PathVariable Long id) {
        Optional<Game> optionalTicket = gamesRepo.findById(id);

        if (optionalTicket.isPresent()) {
            Game game = optionalTicket.get();
            gamesRepo.delete(game);
            return ResponseEntity.ok("Game Deleted");
        } else {
            return ResponseEntity.notFound().build(); // Return 404 Not Found if ticket with given id doesn't exist
        }
    }

    @DeleteMapping(value = "/teams/{id}")
    public ResponseEntity<Object> deleteTeam(@PathVariable Long id) {
        Optional<Team> optionalTicket = teamsRepo.findById(id);

        if (optionalTicket.isPresent()) {
            Team team = optionalTicket.get();
            teamsRepo.delete(team);
            return ResponseEntity.ok("Team Deleted");
        } else {
            return ResponseEntity.notFound().build(); // Return 404 Not Found if ticket with given id doesn't exist
        }
    }

    @PostMapping(value = "/tickets", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> addTicket(@RequestBody @Valid Ticket ticket) {

        var t = ticketsRepo.save(ticket);
        return ResponseEntity.ok(t);

    }

    @DeleteMapping(value = "/tickets/{id}")
    public ResponseEntity<Object> deleteTicket(@PathVariable Long id) {
        Optional<Ticket> optionalTicket = ticketsRepo.findById(id);

        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            ticketsRepo.delete(ticket);
            return ResponseEntity.ok("Ticket Deleted");
        } else {
            return ResponseEntity.notFound().build(); // Return 404 Not Found if ticket with given id doesn't exist
        }
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

}
