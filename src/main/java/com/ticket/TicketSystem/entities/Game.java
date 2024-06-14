/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ticket.TicketSystem.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Date;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Carron Muleya
 */
@Entity
@Table(name = "Games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="home_team_id",nullable=false)
    private Team home_team;

    @ManyToOne
    @JoinColumn(name="away_team_id",nullable=false)
    private Team away_team;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Date kickoff;

  
    private String feature_img;
    
    
     @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<GameTicket> tickets;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the home_team
     */
    public Team getHome_team() {
        return home_team;
    }

    /**
     * @param home_team the home_team to set
     */
    public void setHome_team(Team home_team) {
        this.home_team = home_team;
    }

    /**
     * @return the away_team
     */
    public Team getAway_team() {
        return away_team;
    }

    /**
     * @param away_team the away_team to set
     */
    public void setAway_team(Team away_team) {
        this.away_team = away_team;
    }

    /**
     * @return the kickoff
     */
    public Date getKickoff() {
        return kickoff;
    }

    /**
     * @param kickoff the kickoff to set
     */
    public void setKickoff(Date kickoff) {
        this.kickoff = kickoff;
    }

   

    /**
     * @return the tickets
     */
    public List<GameTicket> getTickets() {
        return (List<GameTicket>) tickets;
    }

    /**
     * @param tickets the tickets to set
     */
    public void setTickets(List<GameTicket> tickets) {
        this.tickets = tickets;
    }

    /**
     * @return the feature_img
     */
    public String getFeature_img() {
        return feature_img;
    }

    /**
     * @param feature_img the feature_img to set
     */
    public void setFeature_img(String feature_img) {
        this.feature_img = feature_img;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the name
     */
    public String getType() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setType(String name) {
        this.name = name;
    }


}
