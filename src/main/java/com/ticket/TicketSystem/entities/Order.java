package com.ticket.TicketSystem.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import java.util.Date;
import org.springframework.data.annotation.CreatedDate;

/**
 *
 * @author Carron Muleya
 */
@Entity
@Table(name = "Orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    @NotEmpty(message = "First Name should not be empty")
    private String firstname;
    @Column(nullable = false)
    private String lastname;

    @Email(message = "Invalid email")
    @NotEmpty(message = "Please provide Email")
    private String email_address;

    @Column(nullable = false)
    private String address_1;
    private String address_2;

    @Column(nullable = false)
    private String town;

    @NotEmpty(message = "No phone number provided")
    private String phone;

    @Column(nullable = false)
    @NotEmpty(message = "No payment method provided")
    private String payment_method;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(nullable = false)
    private String order_ticket_qr;
    
    @Column(nullable = false)
    private String uuid;

    private boolean paid = false;

    @CreatedDate
    private Date created_at = new Date();

    /**
     * @return the firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * @param firstname the firstname to set
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * @return the lastname
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * @param lastname the lastname to set
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * @return the address_1
     */
    public String getAddress_1() {
        return address_1;
    }

    /**
     * @param address_1 the address_1 to set
     */
    public void setAddress_1(String address_1) {
        this.address_1 = address_1;
    }

    /**
     * @return the address_2
     */
    public String getAddress_2() {
        return address_2;
    }

    /**
     * @param address_2 the address_2 to set
     */
    public void setAddress_2(String address_2) {
        this.address_2 = address_2;
    }

    /**
     * @return the town
     */
    public String getTown() {
        return town;
    }

    /**
     * @param town the town to set
     */
    public void setTown(String town) {
        this.town = town;
    }

    /**
     * @return the payment_method
     */
    public String getPayment_method() {
        return payment_method;
    }

    /**
     * @param payment_method the payment_method to set
     */
    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    /**
     * @return the ticket
     */
    public Ticket getTicket() {
        return ticket;
    }

    /**
     * @param ticket the ticket to set
     */
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    /**
     * @return the order_ticket_qr
     */
    public String getOrder_ticket_qr() {
        return order_ticket_qr;
    }

    /**
     * @param order_ticket_qr the order_ticket_qr to set
     */
    public void setOrder_ticket_qr(String order_ticket_qr) {
        this.order_ticket_qr = order_ticket_qr;
    }

    /**
     * @return the created_at
     */
    public Date getCreated_at() {
        return created_at;
    }

    /**
     * @param created_at the created_at to set
     */
    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
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

    /**
     * @return the email_address
     */
    public String getEmail_address() {
        return email_address;
    }

    /**
     * @param email_address the email_address to set
     */
    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the paid
     */
    public boolean isPaid() {
        return paid;
    }

    /**
     * @param paid the paid to set
     */
    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
