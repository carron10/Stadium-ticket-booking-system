/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ticket.TicketSystem.repositories;

import com.ticket.TicketSystem.entities.Game;
import com.ticket.TicketSystem.entities.GameTicket;
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
public interface GameTicketRepository extends CrudRepository<GameTicket, Long> {
    @Query("SELECT t FROM GameTicket t WHERE LOWER(t.ticket.type)=LOWER(:type) AND game=:game")
    public GameTicket findByTypeAndGameIdIgnoreCase(@Param("type") String type,@Param("game") Game game);
}
