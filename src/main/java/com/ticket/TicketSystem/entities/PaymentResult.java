package com.ticket.TicketSystem.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "PaymentResults")
public class PaymentResult {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Other properties remain the same as before

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;
    
    private String reference;
    private String paynowreference;
    private double amount;
    private String status;
    private String pollurl;
    private String hash;

    // Constructors, getters, and setters
    public PaymentResult() {}

    public PaymentResult(String reference, String paynowreference, double amount, String status, String pollurl, String hash) {
        this.reference = reference;
        this.paynowreference = paynowreference;
        this.amount = amount;
        this.status = status;
        this.pollurl = pollurl;
        this.hash = hash;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getPaynowreference() {
        return paynowreference;
    }

    public void setPaynowreference(String paynowreference) {
        this.paynowreference = paynowreference;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPollurl() {
        return pollurl;
    }

    public void setPollurl(String pollurl) {
        this.pollurl = pollurl;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * @return the order
     */
    public Order getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(Order order) {
        this.order = order;
    }
    public boolean isPaid(){
        return "Paid".equals(this.getStatus());
    }

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
}
