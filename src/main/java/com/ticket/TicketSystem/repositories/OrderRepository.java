/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.ticket.TicketSystem.repositories;

import org.springframework.data.repository.CrudRepository;

import com.ticket.TicketSystem.entities.Order;
import org.springframework.stereotype.Repository;


/**
 *
 * @author Carron Muleya
 */
@Repository
public interface OrderRepository extends CrudRepository<Order,Long> {
    Order findByIdAndUuid(Long id,String uuid);
}
