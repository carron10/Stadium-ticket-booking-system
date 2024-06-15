package com.ticket.TicketSystem;

import com.ticket.TicketSystem.Security.SecurityConfiguration;
import com.ticket.TicketSystem.api.ApiControllers;
import com.ticket.TicketSystem.entities.Game;
import com.ticket.TicketSystem.entities.GameTicket;
import com.ticket.TicketSystem.entities.Order;
import com.ticket.TicketSystem.mail.EmailServiceImpl;
import com.ticket.TicketSystem.repositories.ContactUsRepository;
import com.ticket.TicketSystem.repositories.GameRepository;
import com.ticket.TicketSystem.repositories.GameTicketRepository;
import com.ticket.TicketSystem.repositories.OrderRepository;
import com.ticket.TicketSystem.repositories.PaymentResultsRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.spring6.SpringTemplateEngine;

@WebMvcTest(ApiControllers.class)
@Import(SecurityConfiguration.class)
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
public class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AppUtils utils;

    @MockBean
    GameRepository gameRepo;
    @MockBean
    GameTicketRepository gameticketRepo;
    @MockBean
    OrderRepository orderRepo;

    @MockBean
    PaymentResultsRepo paymentrepo;

//    @MockBean
//    private ObjectToMapConvert objectToMapConvert;

    @MockBean
    ContactUsRepository contacts;
        
    @MockBean
    SpringTemplateEngine templateEngine;

    @MockBean
    EmailServiceImpl emailService;

    @MockBean
    private ResourceLoader resourceLoader;

    @MockBean
    CustomElementFactoryImpl CustomElementFactoryImpl;

    @MockBean
    PaymentService paymentService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPlaceOrder_NoTicketSpecified() throws Exception {
        Order order = new Order();
        order.setEmail_address("test@example.com");
        order.setPhone("771013028");
        order.setAddress_1("Test address");
        order.setFirstname("Lucky");
        order.setLastname("Dzimbi");
        order.setPayment_method("paynow");
        order.setTown("Byo");
       
       

        mockMvc.perform(MockMvcRequestBuilders.post("/api/place_order")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .flashAttr("order", order))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("error no ticket specified")));
    }

    @Test
    public void testPlaceOrder_NoTicketsAvailable() throws Exception {
        Game game = new Game();
        GameTicket ticket = new GameTicket();
        ticket.setGame(game);
        ticket.setQuantity(0);

        Order order = new Order();
        order.setEmail_address("test@example.com");
        order.setPhone("771013028");
        order.setAddress_1("Test address");
        order.setFirstname("Lucky");
        order.setLastname("Dzimbi");
        order.setPayment_method("paynow");
        order.setTown("Byo");
        order.setEmail_address("test@example.com");
        order.setTicket(ticket);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/place_order")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .flashAttr("order", order))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("No more tickets available in stock")));
    }

    @Test
    public void testPlaceOrder_MaxOrderLimitReached() throws Exception {
        Game game = new Game();
        game.setId(1L);

        GameTicket ticket = new GameTicket();
        ticket.setGame(game);
        ticket.setQuantity(10);

        Order order = new Order();
        order.setEmail_address("test@example.com");
        order.setPhone("771013028");
        order.setAddress_1("Test address");
        order.setFirstname("Lucky");
        order.setLastname("Dzimbi");
        order.setPayment_method("paynow");
        order.setTown("Byo");
        order.setEmail_address("test@example.com");
        order.setTicket(ticket);

        order.setTicket(ticket);

        List<Order> existingOrders = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            existingOrders.add(new Order());
        }

        when(orderRepo.findByEmailAndGame(anyString(), anyLong())).thenReturn(existingOrders);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/place_order")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .flashAttr("order", order))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("You have reached the maximum limit of purcasing order")));
    }

    @Test
    public void testPlaceOrder_Success() throws Exception {
        Game game = new Game();
        game.setId(1L);

        GameTicket ticket = new GameTicket();
        ticket.setGame(game);
        ticket.setQuantity(10);

        Order order = new Order();
        order.setEmail_address("test@example.com");
        order.setPhone("771013028");
        order.setAddress_1("Test address");
        order.setFirstname("Lucky");
        order.setLastname("Dzimbi");
        order.setPayment_method("paynow");
        order.setTown("Byo");
        order.setEmail_address("test@example.com");
        order.setTicket(ticket);
        order.setPayment_method("bank_card");

        when(orderRepo.findByEmailAndGame(anyString(), anyLong())).thenReturn(new ArrayList<>());
        when(orderRepo.save(any(Order.class))).thenReturn(order);
        when(paymentService.initPayment(any(Order.class))).thenReturn("http://redirect.url");

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/api/place_order")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .flashAttr("order", order))
                .andExpect(status().is3xxRedirection())
                .andReturn().getResponse();

        assertEquals("http://redirect.url", response.getRedirectedUrl());
    }
}
