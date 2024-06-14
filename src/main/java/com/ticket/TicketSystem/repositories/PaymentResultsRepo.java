/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.ticket.TicketSystem.repositories;

import com.ticket.TicketSystem.entities.PaymentResult;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Carron Muleya
 */
@Repository
public interface PaymentResultsRepo extends CrudRepository<PaymentResult,Long> {
    
}
