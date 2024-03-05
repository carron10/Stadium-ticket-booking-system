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

    @Column(nullable = false)
    private String home_team;

    @Column(nullable = false)
    private String away_team;
    private String type;
    
    @Column(nullable = false)
    private Date kickoff;

    private String away_team_logo;
    private String home_team_logo;
    private String feature_img;
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Ticket> tickets;

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
    public String getHome_team() {
        return home_team;
    }

    /**
     * @param home_team the home_team to set
     */
    public void setHome_team(String home_team) {
        this.home_team = home_team;
    }

    /**
     * @return the away_team
     */
    public String getAway_team() {
        return away_team;
    }

    /**
     * @param away_team the away_team to set
     */
    public void setAway_team(String away_team) {
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
     * @return the away_team_logo
     */
    public String getAway_team_logo() {
        return away_team_logo;
    }

    /**
     * @param away_team_logo the away_team_logo to set
     */
    public void setAway_team_logo(String away_team_logo) {
        this.away_team_logo = away_team_logo;
    }

    /**
     * @return the home_team_logo
     */
    public String getHome_team_logo() {
        return home_team_logo;
    }

    /**
     * @param home_team_logo the home_team_logo to set
     */
    public void setHome_team_logo(String home_team_logo) {
        this.home_team_logo = home_team_logo;
    }

    /**
     * @return the tickets
     */
    public List<Ticket> getTickets() {
        return (List<Ticket>) tickets;
    }

    /**
     * @param tickets the tickets to set
     */
    public void setTickets(List<Ticket> tickets) {
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
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

}
