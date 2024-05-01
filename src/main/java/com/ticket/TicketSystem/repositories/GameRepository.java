/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.ticket.TicketSystem.repositories;

import com.ticket.TicketSystem.entities.Game;
import java.util.List;
import org.springframework.data.domain.Limit;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Carron Muleya
 */
public interface GameRepository extends CrudRepository<Game,Long> {

    public Game findById(int game);
    public List<Game> findByOrderByKickoffDesc(Limit limit);
}
