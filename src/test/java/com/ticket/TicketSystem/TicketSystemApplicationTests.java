package com.ticket.TicketSystem;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;
import java.util.HashMap;
import org.springframework.stereotype.Component;
import com.ticket.TicketSystem.entities.EmailDetails;
import com.ticket.TicketSystem.entities.PaymentResult;
import static junit.framework.Assert.assertEquals;

import org.springframework.test.web.servlet.MockMvc;

class TicketSystemApplicationTests extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void testIndexPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString("Welcome To BabourFields Ticket Booking System"))); // Replace with actual expected content
    }

//    @Test
//    public void givenJavaObject_whenUsingJackson_thenConvertToMap() {
//       PaymentResult paymentResult=new PaymentResult();
//      paymentResult.setHash("JSJJS");
//      
//       Map map=ObjectToMapConvert.convertToMap(paymentResult);
//       assertEquals(paymentResult.getHash(), map.get("hash"));
//    }
}
