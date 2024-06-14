/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ticket.TicketSystem;

import com.ticket.TicketSystem.entities.GameTicket;
import com.ticket.TicketSystem.entities.Order;
import com.ticket.TicketSystem.entities.Ticket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import zw.co.paynow.core.Payment;
import zw.co.paynow.core.Paynow;
import zw.co.paynow.responses.StatusResponse;
import zw.co.paynow.responses.WebInitResponse;


@Component
public class PaymentService {
    
    @Value("${paynow.integration.id}")
    String integrationid;
    
    @Value("${paynow.integration.key}")
    String integrationkey;
    
    @Value("${paynow.results.domain_url}")
    String results_domain_url;
    
    @Value("${paynow.return.domain_url}")
    String return_domain_url;
    
    public String initPayment(Order order) {
        GameTicket t=order.getTicket();
        
        Paynow paynow = new Paynow(integrationid, integrationkey);
        paynow.setReturnUrl(return_domain_url+"/payment/return/" + order.getId() + "/" + order.getUuid());
        paynow.setResultUrl(results_domain_url+"/api/payment/results/" + order.getId() + "/" + order.getUuid());

        Payment payment = paynow.createPayment("Invoice " + order.getId());

        // Passing in the name of the item and the price of the item
        payment.add("Tickect", t.getPrice());
//        payment.add("Apples", 3.4);
        //Initiating the transaction
        WebInitResponse response = paynow.send(payment);
        //If a mobile transaction,
        //MobileInitResponse response = paynow.sendMobile(payment, "0771234567", MobileMoneyMethod.ECOCASH);

        if (response.isRequestSuccess()) {
            // Get the url to redirect the user to so they can make payment
            String redirectURL = response.redirectURL();
            // Get the poll url of the transaction
            String pollUrl = response.pollUrl();

            //checking if the payment has been paid
            StatusResponse status = paynow.pollTransaction(pollUrl);

            System.out.println(pollUrl);
            System.out.println("Status: " + status.paid());

            if (status.paid()) {
                // Yay! Transaction was paid for
            } else {
                System.out.println("Why you no pay?");
            }
            return redirectURL;
        } else {
            // Something went wrong
            System.out.println(response.errors());
        }
        return null;

    }

}
