/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ticket.TicketSystem.repositories;

import com.ticket.TicketSystem.entities.Game;
import com.ticket.TicketSystem.entities.Ticket;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Carron Muleya
 */
@Repository
public interface TicketRepository extends CrudRepository<Ticket, Long> {
  
    public Ticket findByType(String type);
    
//    @Query("SELECT t FROM Ticket t WHERE LOWER(t.type)=LOWER(:type) AND game=:game")
//    public Ticket findByTypeAndGameIdIgnoreCase(@Param("type") String type,@Param("game") Game game);
}
