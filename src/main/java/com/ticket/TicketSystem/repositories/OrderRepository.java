/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.ticket.TicketSystem.repositories;

import com.ticket.TicketSystem.entities.Game;
import org.springframework.data.repository.CrudRepository;

import com.ticket.TicketSystem.entities.Order;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Carron Muleya
 */
@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

    Order findByIdAndUuid(Long id, String uuid);

    @Query("SELECT o FROM Order o WHERE o.email_address=:email AND o.ticket.game.id=:game_id AND o.paid=true")
    List<Order> findByEmailAndGame(String email,Long game_id); //To get all paid order for certain email and game

    @Query("SELECT o FROM Order o WHERE o.ticket.id=:ticket_id AND o.ticket.game.id=:game_id")
    List<Order> findByTicketAndGame(@Param("ticket_id") Long ticketId, @Param("game_id") Long gameId); //To get all olders of a certain Ticket_ID and Game
}
